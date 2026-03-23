package com.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.context.BaseContext;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.leave.LeaveAddDTO;
import com.ems.domain.dto.leave.LeaveApproveDTO;
import com.ems.domain.dto.page.LeavePageDTO;
import com.ems.domain.pojo.Dept;
import com.ems.domain.pojo.Emp;
import com.ems.domain.pojo.LeaveRequest;
import com.ems.domain.vo.leave.LeaveRecordVO;
import com.ems.mapper.DeptMapper;
import com.ems.mapper.EmpMapper;
import com.ems.mapper.LeaveMapper;
import com.ems.mapper.RoleMapper;
import com.ems.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, LeaveRequest> implements LeaveService {

    private final LeaveMapper leaveMapper;
    private final EmpMapper empMapper;
    private final DeptMapper deptMapper;
    private final RoleMapper roleMapper;

    /**
     * 分页查询请假申请
     * @param leavePageDTO 请假申请分页查询参数
     * @return 请假申请分页查询结果
     */
    public PageResult pageQuery(LeavePageDTO leavePageDTO) {
        // 获取当前登录用户
        Long currentEmpId = BaseContext.getCurrentId();
        String roleKey = roleMapper.getRoleKeyByEmpId(currentEmpId);
        
        // 分页条件
        Page<LeaveRequest> page = Page.of(leavePageDTO.getPageNum(), leavePageDTO.getPageSize());

        // 构建查询条件
        var query = lambdaQuery();
        
        // 根据角色限制数据查询范围
        if ("admin".equals(roleKey) || "hr".equals(roleKey)) {
            // 管理员可以查看所有记录
            if (StringUtils.isNotBlank(leavePageDTO.getEmpName())) {
                query.like(LeaveRequest::getEmpName, leavePageDTO.getEmpName());
            }
        } else if ("manager".equals(roleKey)) {
            // 部门主管只能查看本部门的记录
            Long deptId = empMapper.selectById(currentEmpId).getDeptId();
            List<Long> deptEmpIds = empMapper.getEmpIdsByDeptId(deptId);
            query.in(LeaveRequest::getEmpId, deptEmpIds);
            
            if (StringUtils.isNotBlank(leavePageDTO.getEmpName())) {
                query.like(LeaveRequest::getEmpName, leavePageDTO.getEmpName());
            }
        } else {
            // 普通员工只能查看自己的记录
            query.eq(LeaveRequest::getEmpId, currentEmpId);
        }

        // 添加其他查询条件
        query.eq(leavePageDTO.getLeaveType() != null, LeaveRequest::getLeaveType, leavePageDTO.getLeaveType())
                .eq(leavePageDTO.getOverallStatus() != null, LeaveRequest::getOverallStatus, leavePageDTO.getOverallStatus())
                .ge(leavePageDTO.getStartDate() != null, LeaveRequest::getStartDate, leavePageDTO.getStartDate())
                .le(leavePageDTO.getEndDate() != null, LeaveRequest::getEndDate, leavePageDTO.getEndDate())
                .orderByDesc(LeaveRequest::getCreateTime);

        // 执行查询
        Page<LeaveRequest> p = query.page(page);

        // 转换为VO对象
        List<LeaveRecordVO> records = p.getRecords().stream()
                .map(leave -> {
                    LeaveRecordVO leaveVO = BeanUtil.copyProperties(leave, LeaveRecordVO.class);
                    
                    // 设置部门名称
                    Emp emp = empMapper.selectById(leave.getEmpId());
                    if (emp != null) {
                        Dept dept = deptMapper.selectById(emp.getDeptId());
                        if (dept != null) {
                            leaveVO.setDeptName(dept.getName());
                        }
                    }
                    
                    // 设置审批人姓名
                    if (leave.getLeaderApprovedBy() != null) {
                        leaveVO.setLeaderApprovedByName(empMapper.getNameById(leave.getLeaderApprovedBy()));
                    }
                    if (leave.getHrApprovedBy() != null) {
                        leaveVO.setHrApprovedByName(empMapper.getNameById(leave.getHrApprovedBy()));
                    }
                    
                    // 设置是否可以修改（管理员或记录所有者且未开始审批）
                    boolean canEdit = "admin".equals(roleKey) || "hr".equals(roleKey) ||
                            (currentEmpId.equals(leave.getEmpId()) && leave.getOverallStatus() == 0);
                    leaveVO.setCanEdit(canEdit);
                    
                    return leaveVO;
                })
                .toList();

        return PageResult.builder()
                .total(p.getTotal())
                .records(records)
                .build();
    }

    /**
     * 批量删除请假记录
     * @param ids 请假记录ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        // 只有管理员可以删除记录
        String roleKey = roleMapper.getRoleKeyByEmpId(BaseContext.getCurrentId());
        if (!"admin".equals(roleKey)) {
            throw new RuntimeException("没有权限执行此操作");
        }
        
        removeBatchByIds(ids);
    }

    /**
     * 批量审批请假申请
     * @param leaveApproveDTO 请假审批参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveBatch(LeaveApproveDTO leaveApproveDTO) {
        // 获取当前用户角色
        Long currentEmpId = BaseContext.getCurrentId();
        String roleKey = roleMapper.getRoleKeyByEmpId(currentEmpId);
        LocalDateTime now = LocalDateTime.now();
        
        // 获取请假记录列表
        List<LeaveRequest> leaveList = listByIds(leaveApproveDTO.getIds());
        if (leaveList.isEmpty()) {
            throw new RuntimeException("请假记录不存在");
        }
        
        // 部门主管审批
        if (leaveApproveDTO.getType() == 1) {
            if (!"manager".equals(roleKey)) {
                throw new RuntimeException("只有部门主管可以进行此审批");
            }
            
            // 获取当前部门主管的部门ID
            Long deptId = empMapper.selectById(currentEmpId).getDeptId();
            
            // 批量处理每条请假记录
            for (LeaveRequest leave : leaveList) {
                // 检查是否是本部门的请假申请
                Long empDeptId = empMapper.selectById(leave.getEmpId()).getDeptId();
                if (!deptId.equals(empDeptId)) {
                    throw new RuntimeException("只能审批本部门的请假申请，员工：" + leave.getEmpName());
                }
                
                // 检查是否已经审批过
                if (!Objects.equals(leave.getLeaderStatus(), 0)) {
                    throw new RuntimeException("请假申请已审批过，员工：" + leave.getEmpName());
                }
                
                // 更新领导审批状态
                leave.setLeaderStatus(leaveApproveDTO.getStatus());
                leave.setLeaderApprovedBy(currentEmpId);
                leave.setLeaderApprovedTime(now);
                
                // 如果驳回，直接更新总状态
                if (leaveApproveDTO.getStatus() == 2) {
                    leave.setOverallStatus(2);
                }
                
                // 更新记录
                leave.setUpdateTime(now);
                leave.setUpdateUser(currentEmpId);
            }
        }
        // 人事审批
        else if (leaveApproveDTO.getType() == 2) {
            if (!"hr".equals(roleKey)) {
                throw new RuntimeException("只有人事可以进行此审批");
            }
            
            // 批量处理每条请假记录
            for (LeaveRequest leave : leaveList) {
                // 检查是否已经审批过
                if (!Objects.equals(leave.getHrStatus(), 0)) {
                    throw new RuntimeException("请假申请已审批过，员工：" + leave.getEmpName());
                }
                
                // 检查是否已经过领导审批
                if (!Objects.equals(leave.getLeaderStatus(), 1)) {
                    throw new RuntimeException("请等待部门主管审批通过后再审批，员工：" + leave.getEmpName());
                }
                
                // 更新人事审批状态
                leave.setHrStatus(leaveApproveDTO.getStatus());
                leave.setHrApprovedBy(currentEmpId);
                leave.setHrApprovedTime(now);
                
                // 更新总状态
                leave.setOverallStatus(leaveApproveDTO.getStatus());
                
                // 更新记录
                leave.setUpdateTime(now);
                leave.setUpdateUser(currentEmpId);
            }
        }
        
        // 批量更新
        updateBatchById(leaveList);
    }

    /**
     * 获取待审批的请假申请
     * @param leavePageDTO 分页参数
     * @return 待审批的请假申请列表
     */
    public PageResult getPendingApprovals(LeavePageDTO leavePageDTO) {
        // 获取当前用户角色
        Long currentEmpId = BaseContext.getCurrentId();
        String roleKey = roleMapper.getRoleKeyByEmpId(currentEmpId);
        
        // 分页条件
        Page<LeaveRequest> page = Page.of(leavePageDTO.getPageNum(), leavePageDTO.getPageSize());
        
        // 构建查询条件
        var query = lambdaQuery();
        if("admin".equals(roleKey)){
            // 管理员可以看到所有待审批记录
            query.eq(LeaveRequest::getOverallStatus, 0);
        } else if ("manager".equals(roleKey)) {
            // 部门主管只能看到本部门的待审批记录
            Long deptId = empMapper.selectById(currentEmpId).getDeptId();
            List<Long> deptEmpIds = empMapper.getEmpIdsByDeptId(deptId);
            query.in(LeaveRequest::getEmpId, deptEmpIds)
                    .eq(LeaveRequest::getLeaderStatus, 0);
        } else if ("hr".equals(roleKey)) {
            // 人事只能看到领导已审批通过的记录
            query.eq(LeaveRequest::getLeaderStatus, 1)
                    .eq(LeaveRequest::getHrStatus, 0);
        } else {
            throw new RuntimeException("没有权限查看待审批记录");
        }
        
        // 添加其他查询条件
        query.like(StringUtils.isNotBlank(leavePageDTO.getEmpName()),LeaveRequest::getEmpName, leavePageDTO.getEmpName());
        query.eq(leavePageDTO.getLeaveType() != null, LeaveRequest::getLeaveType, leavePageDTO.getLeaveType());
        query.between(leavePageDTO.getStartDate() != null && leavePageDTO.getEndDate() != null, LeaveRequest::getStartDate, leavePageDTO.getStartDate(), leavePageDTO.getEndDate());
        query.orderByDesc(LeaveRequest::getCreateTime);
        
        // 执行查询
        Page<LeaveRequest> p = query.page(page);
        
        // 转换为VO对象
        List<LeaveRecordVO> records = p.getRecords().stream()
                .map(leave -> {
                    LeaveRecordVO leaveVO = BeanUtil.copyProperties(leave, LeaveRecordVO.class);
                    
                    // 设置部门名称
                    Emp emp = empMapper.selectById(leave.getEmpId());
                    if (emp != null) {
                        Dept dept = deptMapper.selectById(emp.getDeptId());
                        if (dept != null) {
                            leaveVO.setDeptName(dept.getName());
                        }
                    }
                    
                    // 设置审批人姓名
                    if (leave.getLeaderApprovedBy() != null) {
                        leaveVO.setLeaderApprovedByName(empMapper.getNameById(leave.getLeaderApprovedBy()));
                    }
                    if (leave.getHrApprovedBy() != null) {
                        leaveVO.setHrApprovedByName(empMapper.getNameById(leave.getHrApprovedBy()));
                    }
                    
                    return leaveVO;
                })
                .toList();

        return PageResult.builder()
                .total(p.getTotal())
                .records(records)
                .build();
    }

    /**
     * 新增请假申请
     * @param leaveAddDTO 请假申请参数
     */
    public void addLeave(LeaveAddDTO leaveAddDTO) {
        LeaveRequest request = BeanUtil.copyProperties(leaveAddDTO, LeaveRequest.class);
        Emp emp = empMapper.selectById(BaseContext.getCurrentId());
        request.setEmpId(emp.getId());
        request.setEmpName(emp.getName());
        save(request);
    }
}
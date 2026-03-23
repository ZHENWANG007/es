package com.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.domain.dto.dept.DeptAddDTO;
import com.ems.domain.pojo.Dept;
import com.ems.domain.pojo.Emp;
import com.ems.domain.vo.dept.DeptTreeVO;
import com.ems.domain.vo.dept.DeptVO;
import com.ems.mapper.DeptMapper;
import com.ems.mapper.EmpMapper;
import com.ems.service.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    private final DeptMapper deptMapper;
    private final EmpMapper empMapper;

    /**
     * 递归排序子部门
     * @param deptTreeVO 部门树节点
     */
    private void sortChildren(DeptTreeVO deptTreeVO) {
        if (deptTreeVO.getChildren() != null && !deptTreeVO.getChildren().isEmpty()) {
            // 对当前节点的子节点进行排序
            deptTreeVO.setChildren(deptTreeVO.getChildren().stream()
                    .sorted(Comparator.comparing(DeptTreeVO::getOrderNum))
                    .collect(Collectors.toList()));
            // 递归对每个子节点的子节点进行排序
            deptTreeVO.getChildren().forEach(this::sortChildren);
        }
    }

    /**
     * 获取部门树形结构
     * @return 部门树形结构列表
     */
    public List<DeptTreeVO> getDeptTree(String flag) {

        List<Dept> deptList = new ArrayList<>();
        if (flag != null && flag.equals("TREE")){
            // 1. 查询所有未禁用的部门
            deptList = list(new LambdaQueryWrapper<Dept>()
                        .eq(Dept::getStatus, 0) // 未禁用
                        .orderByAsc(Dept::getParentId));
        }else if (flag != null && flag.equals("LIST")){
            // 1. 查询所有部门
            deptList = list(new LambdaQueryWrapper<Dept>()
                    .orderByAsc(Dept::getParentId));
        }


        // 2. 将部门列表转换为DeptTreeVO列表
        List<DeptTreeVO> deptTreeVOList = deptList.stream()
                .map(dept -> {
                    DeptTreeVO deptTreeVO = new DeptTreeVO();
                    BeanUtil.copyProperties(dept, deptTreeVO);
                    return deptTreeVO;
                })
                .toList();

        // 3. 构建树形结构
        // 3.1 将部门列表转换为Map，key为部门id，value为DeptTreeVO对象
        Map<Long, DeptTreeVO> deptMap = deptTreeVOList.stream()
                .collect(Collectors.toMap(DeptTreeVO::getId, dept -> dept));

        // 3.2 构建树形结构
        List<DeptTreeVO> tree = new ArrayList<>();
        deptTreeVOList.forEach(dept -> {
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                // 如果是顶级部门，直接添加到结果列表中
                tree.add(dept);
            } else {
                // 如果不是顶级部门，找到其父部门，将其添加到父部门的children中
                DeptTreeVO parent = deptMap.get(dept.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dept);
                }
            }
        });

        // 4. 对树形结构进行排序
        // 4.1 对顶层节点进行排序
        tree.sort(Comparator.comparing(DeptTreeVO::getOrderNum));
        // 4.2 递归对所有子节点进行排序
        tree.forEach(this::sortChildren);

        return tree;
    }

    /**
     * 删除部门及其子部门和员工
     * @param id 部门ID
     */
    @Transactional
    public void deleteDept(Long id) {
        // 1. 检查部门是否存在
        Dept dept = getById(id);
        if (dept == null) {
            throw new RuntimeException("部门不存在");
        }

        // 2. 检查是否有子部门
        long count = count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, id));

        if (count > 0) {
            throw new RuntimeException("该部门下存在子部门，不能删除");
        }
        // 4.查询该部门下的员工
        List<Long> empIds = empMapper.selectList(new LambdaQueryWrapper<Emp>()
                .eq(Emp::getDeptId, id))
                .stream()
                .map(Emp::getId)
                .collect(Collectors.toList());

        // 5. 删除部门员工
       empMapper.deleteEmpByDeptId(id);

        // 6. 删除部门员工角色
        if (!empIds.isEmpty()) {
            empMapper.deleteEmpRole(empIds);
        }
        this.removeById(id);
    }


    /**
     * 新增部门
     * @param deptDTO 部门DTO
     */
    public void addDept(DeptAddDTO deptDTO) {

        // 1. 如果有父部门，检查父部门是否存在
        if (deptDTO.getParentId() != null && deptDTO.getParentId() != 0) {
            Dept parentDept = getById(deptDTO.getParentId());
            if (parentDept == null) {
                throw new RuntimeException("父部门不存在");
            }
        }

        // 2. 检查部门名称是否重复
        Dept existDept = getOne(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getName, deptDTO.getName()));

        if (existDept != null){
            throw new RuntimeException("部门名称已存在");
        }

        // 3. 保存新部门信息
        save(BeanUtil.copyProperties(deptDTO, Dept.class));
    }

    /**
     * 修改部门
     * @param deptDTO 部门DTO
     */
    @Transactional
    public void updateDept(DeptAddDTO deptDTO) {

        // 1. 检查部门是否存在
        Dept existDept = getById(deptDTO.getId());
        if (existDept == null) {
            throw new RuntimeException("部门不存在");
        }

        // 2. 检查部门名称是否重复
        long count = count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getName, deptDTO.getName())
                .ne(Dept::getId, deptDTO.getId())); // 忽略自身
        if (count > 0) {
            throw new RuntimeException("部门名称已存在");
        }

        // 3. 如果有父部门，检查父部门是否存在
        if (deptDTO.getParentId() != null && deptDTO.getParentId() != 0) {

            // 不能将部门的父部门设置为自己或自己的子部门
            if (deptDTO.getParentId().equals(deptDTO.getId())) {
                throw new RuntimeException("不能将部门的父部门设置为自己");
            }

            Dept parentDept = getById(deptDTO.getParentId());

            if (parentDept == null) {
                throw new RuntimeException("父部门不存在");
            }

        }

        // 4. 检查部门状态是否发生变化
        Dept newDept = BeanUtil.copyProperties(deptDTO, Dept.class);
        if (!Objects.equals(existDept.getStatus(), newDept.getStatus())) {
            // 如果是父部门，则其子部门的状态也会改变，需要更新该部门下所有员工的状态
            // 1.获取所有子部门ID
            List<Long> childDeptIds = deptMapper.getChildDeptIds(existDept.getId());
            
            // 2. 先更新所有子部门的状态
            if (!childDeptIds.isEmpty()) {
                lambdaUpdate()
                    .set(Dept::getStatus, newDept.getStatus())
                    .in(Dept::getId, childDeptIds)
                    .update();
            }
            
            // 3. 更新该部门及其子部门下所有员工的状态
            childDeptIds.add(deptDTO.getId()); // 添加当前部门ID
            empMapper.updateEmpStatusByDeptIds(childDeptIds, newDept.getStatus());
        }

        // 5. 更新部门信息
        updateById(newDept);
    }

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情
     */
    public DeptVO getDeptDetail(Long id) {

        // 查询部门详情
        Dept dept = getById(id);
        if (dept == null) {
            throw new RuntimeException("部门不存在");
        }
        return BeanUtil.copyProperties(dept, DeptVO.class);
    }

    /**
     * 判断部门是否有员工 是否存在子部门
     * @param id 部门ID
     * @return 是否有员工
     */
    public boolean hasEmp(Long id) {
        // 1.检查是否存在子部门
        long count = count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, id));
        if (count > 0) {
            return true;
        }
        // 1.检查该部门是否有员工
        count = empMapper.selectCount(new LambdaQueryWrapper<Emp>()
                .eq(Emp::getDeptId, id));

        return count > 0;
    }
}
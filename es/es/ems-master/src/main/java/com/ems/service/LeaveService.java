package com.ems.service;

import com.ems.common.result.PageResult;
import com.ems.domain.dto.leave.LeaveAddDTO;
import com.ems.domain.dto.leave.LeaveApproveDTO;
import com.ems.domain.dto.page.LeavePageDTO;

import java.util.List;

public interface LeaveService {

    /**
     * 分页查询
     * @param leavePageDTO 分页查询参数
     * @return 分页结果
     */
    PageResult pageQuery(LeavePageDTO leavePageDTO);

    /**
     * 新增请假记录
     * @param leaveAddDTO 请假信息
     */
    void addLeave(LeaveAddDTO leaveAddDTO);

    /**
     * 批量删除请假记录
     * @param ids 请假记录ID列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 审批请假申请
     * @param leaveApproveDTO 审批信息
     */
    void approveBatch(LeaveApproveDTO leaveApproveDTO);

    /**
     * 获取待审批的请假申请
     * @param leavePageDTO 分页查询参数
     * @return 分页结果
     */
    PageResult getPendingApprovals(LeavePageDTO leavePageDTO);
}

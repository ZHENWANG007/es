package com.ems.controller.admin;

import com.ems.common.result.PageResult;
import com.ems.common.result.Result;
import com.ems.domain.dto.leave.LeaveAddDTO;
import com.ems.domain.dto.leave.LeaveApproveDTO;
import com.ems.domain.dto.page.JobPageDTO;
import com.ems.domain.dto.page.LeavePageDTO;
import com.ems.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("admin/leave")
@Tag(name = "请假申请管理接口")
@Slf4j
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/page")
    @Operation(summary = "请假记录分页查询")
    @PreAuthorize("hasAuthority('ems:leave:list')")
    public Result<PageResult> getList(LeavePageDTO leavePageDTO) {
        log.info("请假记录分页查询，参数：{}", leavePageDTO);
        PageResult pageResult = leaveService.pageQuery(leavePageDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @Operation(summary = "新增请假记录")
    @PreAuthorize("hasAuthority('ems:leave:add')")
    public Result addLeave(@RequestBody LeaveAddDTO leaveAddDTO) {
        log.info("新增请假记录，参数：{}", leaveAddDTO);
        leaveService.addLeave(leaveAddDTO);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除请假记录")
    @PreAuthorize("hasAuthority('ems:leave:delete')")
    public Result batchDelete(@RequestBody List<Long> ids) {
        log.info("批量删除请假记录，ID：{}", ids);
        leaveService.batchDelete(ids);
        return Result.success();
    }

    @PostMapping("/approve")
    @Operation(summary = "批量审批请假申请")
    @PreAuthorize("hasAnyAuthority('ems:leave:approve')")
    public Result approveBatch(@RequestBody LeaveApproveDTO leaveApproveDTO) {
        log.info("批量审批请假申请，参数：{}", leaveApproveDTO);
        leaveService.approveBatch(leaveApproveDTO);
        return Result.success();
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待审批的请假申请")
    @PreAuthorize("hasAnyAuthority('ems:leave:approve')")
    public Result<PageResult> getPendingApprovals(LeavePageDTO leavePageDTO) {
        log.info("获取待审批的请假申请，参数：{}", leavePageDTO);
        PageResult pageResult = leaveService.getPendingApprovals(leavePageDTO);
        return Result.success(pageResult);
    }





}
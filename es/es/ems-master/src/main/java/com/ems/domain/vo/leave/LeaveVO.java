package com.ems.domain.vo.leave;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "请假VO")
public class LeaveVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long empId;

    @Schema(description = "员工姓名")
    private String empName;

    @Schema(description = "请假类型（1-事假、2-病假、3-公假）")
    private Integer leaveType;

    @Schema(description = "开始日期")
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "请假原因")
    private String reason;

    @Schema(description = "直属领导审批状态（0-待审批，1-通过，2-驳回）")
    private Integer leaderStatus;

    @Schema(description = "直属领导审批人ID")
    private Long leaderApprovedBy;

    @Schema(description = "直属领导审批时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaderApprovedTime;

    @Schema(description = "人事经理审批状态（0-待审批，1-通过，2-驳回）")
    private Integer hrStatus;

    @Schema(description = "人事经理审批人ID")
    private Long hrApprovedBy;

    @Schema(description = "人事经理审批时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hrApprovedTime;

    @Schema(description = "请假总状态（0-待审批，1-已批准，2-已驳回）")
    private Integer overallStatus;

    @Schema(description = "创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建者ID")
    private Long createUser;

    @Schema(description = "更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "更新者ID")
    private Long updateUser;
} 
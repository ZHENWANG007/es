package com.ems.domain.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "LeaveRequestVO", description = "请假申请类")
public class LeaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6235476657497418432L;

    @Schema(description = "请假申请ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long empId;

    @Schema(description = "员工姓名")
    private String empName;

    @Schema(description = "请假类型（1-事假、2-病假、3-公假）")
    private Integer leaveType;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "请假原因")
    private String reason;

    @Schema(description = "直属领导审批状态（0-待审批，1-通过，2-驳回）")
    private Integer leaderStatus;

    @Schema(description = "直属领导审批人ID")
    private Long leaderApprovedBy;

    @Schema(description = "直属领导审批时间")
    private LocalDateTime leaderApprovedTime;

    @Schema(description = "人事经理审批状态（0-待审批，1-通过，2-驳回）")
    private Integer hrStatus;

    @Schema(description = "人事经理审批人ID")
    private Long hrApprovedBy;

    @Schema(description = "人事经理审批时间")
    private LocalDateTime hrApprovedTime;

    @Schema(description = "请假总状态（0-待审批，1-已批准，2-已驳回）")
    private Integer overallStatus;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT) //执行插入操作时自动填充
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)//执行插入和更新操作时自动填充
    private LocalDateTime updateTime;

    @Schema(description = "创建者ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @Schema(description = "更新者ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
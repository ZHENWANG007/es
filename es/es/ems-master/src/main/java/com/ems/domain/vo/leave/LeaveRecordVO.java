package com.ems.domain.vo.leave;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "请假记录VO")
@Tag(name = "LeaveRecordVO", description = "请假申请类")
public class LeaveRecordVO {
    
    @Schema(description = "请假ID")
    private Long id;
    
    @Schema(description = "员工ID")
    private Long empId;
    
    @Schema(description = "员工姓名")
    private String empName;
    
    @Schema(description = "部门名称")
    private String deptName;
    
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
    
    @Schema(description = "直属领导审批人姓名")
    private String leaderApprovedByName;
    
    @Schema(description = "直属领导审批时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaderApprovedTime;
    
    @Schema(description = "人事经理审批状态（0-待审批，1-通过，2-驳回）")
    private Integer hrStatus;
    
    @Schema(description = "人事经理审批人姓名")
    private String hrApprovedByName;
    
    @Schema(description = "人事经理审批时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hrApprovedTime;
    
    @Schema(description = "请假总状态（0-待审批，1-已批准，2-已驳回）")
    private Integer overallStatus;

    @Schema(description = "是否可以修改")
    private Boolean canEdit;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

} 
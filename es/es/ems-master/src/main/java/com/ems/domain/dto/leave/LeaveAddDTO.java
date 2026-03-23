package com.ems.domain.dto.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDate;

@Data
@Tag(name = "LeaveAddDTO", description = "请假申请DTO")
public class LeaveAddDTO {

    @Schema(description = "请假类型（1-事假、2-病假、3-公假）")
    private Integer leaveType;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "请假原因")
    private String reason;

}
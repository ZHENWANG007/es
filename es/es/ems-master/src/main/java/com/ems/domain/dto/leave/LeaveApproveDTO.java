package com.ems.domain.dto.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.util.List;

@Data
@Tag(name = "LeaveApproveDTO", description = "审批DTO")
public class LeaveApproveDTO {

    @Schema(description = "请假记录IDS")
    private List<Long> ids;

    @Schema(description = "审批状态（1-通过 2-驳回）")
    private Integer status;

    @Schema(description = "审批类型（1-直属领导（部长）审批 2-人事审批）")
    private Integer type;
}
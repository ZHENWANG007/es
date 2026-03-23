package com.ems.domain.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Jiang YinHui
 * @create 2024-12-31  15:13
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "LeavePageDTO", description = "请假申请分页查询参数")
public class LeavePageDTO extends PageDTO{

    @Schema(description = "员工姓名")
    private String empName;

    @Schema(description = "请假类型（1-事假、2-病假、3-公假）")
    private Integer leaveType;

    @Schema(description = "请假总状态（0-待审批，1-已批准，2-已驳回）")
    private Integer overallStatus;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

}
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
 * @create 2024-12-29  20:23
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "AttendancePageDTO", description = "考勤查询参数")
public class AttendancePageDTO extends PageDTO{

    @Schema(description = "员工姓名")
    private String empName;

    @Schema(description = "部门ID")
    private String deptId;

    @Schema(description = "考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假）")
    private Integer status;

    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;

}
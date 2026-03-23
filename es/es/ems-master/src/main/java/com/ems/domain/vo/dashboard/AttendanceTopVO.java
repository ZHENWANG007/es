package com.ems.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "考勤异常TOP VO")
public class AttendanceTopVO {
    
    @Schema(description = "员工ID")
    private Long empId;
    
    @Schema(description = "员工姓名")
    private String empName;
    
    @Schema(description = "部门名称")
    private String deptName;
    
    @Schema(description = "异常次数")
    private Integer count;
    
    @Schema(description = "总分钟数（迟到/早退/加班时才有）")
    private Integer totalMinutes;
} 
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
@Schema(description = "部门考勤统计VO")
public class DeptAttendanceVO {
    
    @Schema(description = "部门名称")
    private String deptName;
    
    @Schema(description = "总人数")
    private Integer total;
    
    @Schema(description = "出勤率")
    private String attendanceRate;
    
    @Schema(description = "迟到人次")
    private Integer late;
    
    @Schema(description = "早退人次")
    private Integer early;
    
    @Schema(description = "旷工人次")
    private Integer absent;
    
    @Schema(description = "请假人次")
    private Integer leave;
    
    @Schema(description = "加班人次")
    private Integer overtime;
} 
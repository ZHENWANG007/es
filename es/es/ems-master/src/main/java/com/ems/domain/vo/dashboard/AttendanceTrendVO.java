package com.ems.domain.vo.dashboard;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "考勤趋势VO")
public class AttendanceTrendVO {
    
    @Schema(description = "日期")
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate date;
    
    @Schema(description = "出勤率")
    private Integer attendanceRate;
    
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
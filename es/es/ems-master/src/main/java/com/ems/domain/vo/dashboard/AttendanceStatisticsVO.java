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
@Schema(description = "考勤统计VO")
public class AttendanceStatisticsVO {
    
    @Schema(description = "总人数")
    private Integer total;
    
    @Schema(description = "实际出勤人数")
    private Integer present;
    
    @Schema(description = "出勤率")
    private String attendanceRate;
    
    @Schema(description = "考勤汇总")
    private Summary summary;
    
    @Schema(description = "时间统计")
    private TimeStats timeStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
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
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeStats {
        @Schema(description = "总迟到分钟数")
        private Integer totalLateMinutes;
        
        @Schema(description = "总早退分钟数")
        private Integer totalEarlyMinutes;
        
        @Schema(description = "总加班分钟数")
        private Integer totalOvertimeMinutes;
    }
} 
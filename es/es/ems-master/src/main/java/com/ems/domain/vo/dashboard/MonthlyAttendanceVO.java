package com.ems.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "月度考勤统计VO")
public class MonthlyAttendanceVO {
    
    @Schema(description = "当前月份")
    private Integer month;
    
    @Schema(description = "员工总数")
    private Integer total;
    
    @Schema(description = "统计数据")
    private Statistics statistics;
    
    @Schema(description = "月度汇总")
    private Summary summary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        @Schema(description = "日期数组")
        private List<String> dates;
        
        @Schema(description = "每日正常出勤人数")
        private List<Integer> normal;
        
        @Schema(description = "每日迟到人数")
        private List<Integer> late;
        
        @Schema(description = "每日缺勤人数")
        private List<Integer> absent;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        @Schema(description = "工作日天数")
        private Integer normalDays;
        
        @Schema(description = "月度正常出勤总人次")
        private Integer totalNormal;
        
        @Schema(description = "月度迟到总人次")
        private Integer totalLate;
        
        @Schema(description = "月度缺勤总人次")
        private Integer totalAbsent;
        
        @Schema(description = "月度出勤率")
        private String attendanceRate;
    }
} 
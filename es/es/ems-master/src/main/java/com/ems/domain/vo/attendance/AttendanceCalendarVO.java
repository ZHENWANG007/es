package com.ems.domain.vo.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "AttendanceCalendarVO", description = "考勤日历VO")
public class AttendanceCalendarVO {

    /**
     * 日期，格式：YYYY-MM-DD
     */
    @Schema(description = "日期")
    private String date;

    /**
     * 考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假）
     * 多个状态用逗号分隔，如："1,2"表示既迟到又早退
     */
    @Schema(description = "考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假）")
    private String status;

    /**
     * 上班打卡时间，格式：HH:mm 或 HH:mm:ss
     * 未打卡时返回 null 或 "--:--"
     */
    @Schema(description = "上班打卡时间")
    private String checkInTime;

    /**
     * 下班打卡时间，格式：HH:mm 或 HH:mm:ss
     * 未打卡时返回 null 或 "--:--"
     */
    @Schema(description = "下班打卡时间")
    private String checkOutTime;

}
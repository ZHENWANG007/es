package com.ems.domain.vo.attendance;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "AttendanceVO", description = "考勤VO")
public class AttendanceVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long empId;

    @Schema(description = "员工姓名")
    private String empName;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "考勤日期")
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @Schema(description = "上班打卡时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    @Schema(description = "下班打卡时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;

    @Schema(description = "是否迟到（0-否，1-是）")
    private Integer lateFlag;

    @Schema(description = "迟到分钟数")
    private Integer lateMinutes;

    @Schema(description = "是否早退（0-否，1-是）")
    private Integer earlyFlag;

    @Schema(description = "早退分钟数")
    private Integer earlyMinutes;

    @Schema(description = "是否加班（0-否，1-是）")
    private Integer overtimeFlag;

    @Schema(description = "加班分钟数")
    private Integer overtimeMinutes;

    @Schema(description = "是否请假（0-否，1-是）")
    private Integer leaveFlag;

    @Schema(description = "关联请假表ID")
    private Long leaveRequestId;

    @Schema(description = "是否旷工（0-否，1-是）")
    private Integer absentFlag;

    @Schema(description = "备注")
    private String remark;
}
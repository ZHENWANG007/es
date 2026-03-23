package com.ems.controller.admin;

import com.ems.common.result.PageResult;
import com.ems.common.result.Result;
import com.ems.domain.dto.attendance.ClockDTO;
import com.ems.domain.dto.page.AttendancePageDTO;
import com.ems.domain.vo.attendance.AttendanceCalendarVO;
import com.ems.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("admin/attendance")
@Tag(name = "考勤管理接口")
@Slf4j
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/page")
    @Operation(summary = "考勤分页查询")
    @PreAuthorize("hasAnyAuthority('ems:attendance:list')")
    public Result<PageResult> getList(AttendancePageDTO attendancePageDTO) {
        log.info("考勤分页查询，参数{}", attendancePageDTO);
        PageResult pageResult = attendanceService.pageQuery(attendancePageDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/clock")
    @Operation(summary = "员工打卡")
    public Result clock(@RequestBody ClockDTO clockDTO){
        attendanceService.clock(clockDTO);
        return Result.success();
    }


    @GetMapping("/month")
    @Operation(summary = "查询月度打卡记录")
    public Result<List<AttendanceCalendarVO>> monthHistory(@RequestParam String yearMonth) {
        List<AttendanceCalendarVO> list = attendanceService.monthHistory(yearMonth);
        return Result.success(list);
    }

    @GetMapping("/myAttendanceCount")
    @Operation(summary = "查询个人打卡记录统计")
    public Result<Map<Integer, Integer>> myAttendanceCount() {
        return Result.success(attendanceService.myAttendanceCount());
    }

    @GetMapping("/myAttendanceToday")
    @Operation(summary = "查询个人今天打卡记录")
    public Result<Map<String, Integer>> myAttendanceToday() {
        return Result.success(attendanceService.myAttendanceToday());
    }



}
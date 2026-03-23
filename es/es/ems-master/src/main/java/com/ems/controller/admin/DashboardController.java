package com.ems.controller.admin;

import com.ems.common.result.Result;
import com.ems.domain.vo.dashboard.AttendanceStatisticsVO;
import com.ems.domain.vo.dashboard.AttendanceTopVO;
import com.ems.domain.vo.dashboard.AttendanceTrendVO;
import com.ems.domain.vo.dashboard.DeptAttendanceVO;
import com.ems.domain.vo.dashboard.DeptEmpCountVO;
import com.ems.domain.vo.dashboard.MonthlyAttendanceVO;
import com.ems.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("admin/dashboard")
@Tag(name = "数据分析展板接口")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/emp/count")
    @Operation(summary = "获取员工总数")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<Integer> getEmpCount() {
        log.info("获取员工总数");
        return Result.success(dashboardService.getEmpCount());
    }

    @GetMapping("/leave/count")
    @Operation(summary = "获取今天请假人数")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<Integer> getLeaveCount() {
        log.info("获取今天请假人数");
        return Result.success(dashboardService.getLeaveCount());
    }

    @GetMapping("/attendance/count")
    @Operation(summary = "获取今天考勤异常人数")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<Integer> getAttendanceCount() {
        log.info("获取今天考勤异常人数");
        return Result.success(dashboardService.getAttendanceCount());
    }

    @GetMapping("/attendance/statistics")
    @Operation(summary = "获取考勤统计数据")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<AttendanceStatisticsVO> getAttendanceStatistics() {
        log.info("获取考勤统计数据");
        AttendanceStatisticsVO statistics = dashboardService.getAttendanceStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/attendance/trend")
    @Operation(summary = "获取考勤趋势数据")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<List<AttendanceTrendVO>> getAttendanceTrend(
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        log.info("获取考勤趋势数据，天数：{}", days);
        List<AttendanceTrendVO> trend = dashboardService.getAttendanceTrend(days);
        return Result.success(trend);
    }

    @GetMapping("/attendance/dept")
    @Operation(summary = "获取部门考勤统计")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<List<DeptAttendanceVO>> getDeptAttendance() {
        log.info("获取部门考勤统计");
        List<DeptAttendanceVO> deptStats = dashboardService.getDeptAttendance();
        return Result.success(deptStats);
    }

    @GetMapping("/attendance/top")
    @Operation(summary = "获取考勤异常TOP数据")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<List<AttendanceTopVO>> getAttendanceTop(
            @RequestParam(required = false, defaultValue = "4") Integer limit,
            @RequestParam(required = false, defaultValue = "1") Integer type) {
        log.info("获取考勤异常TOP数据，数量：{}，类型：{}", limit, type);
        List<AttendanceTopVO> topList = dashboardService.getAttendanceTop(limit, type);
        return Result.success(topList);
    }

    @GetMapping("/dept/emp/count")
    @Operation(summary = "获取部门员工数量统计")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<List<DeptEmpCountVO>> getDeptEmpCount() {
        log.info("获取部门员工数量统计");
        List<DeptEmpCountVO> stats = dashboardService.getDeptEmpCount();
        return Result.success(stats);
    }

    @GetMapping("/attendance/monthly")
    @Operation(summary = "获取月度考勤统计数据")
    @PreAuthorize("hasAuthority('ems:dashboard:view')")
    public Result<MonthlyAttendanceVO> getMonthlyAttendance(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        // 如果未指定年月，使用当前年月
        LocalDate now = LocalDate.now();
        year = year != null ? year : now.getYear();
        month = month != null ? month : now.getMonthValue();
        
        log.info("获取{}年{}月考勤统计数据", year, month);
        MonthlyAttendanceVO monthlyStats = dashboardService.getMonthlyAttendance(year, month);
        return Result.success(monthlyStats);
    }
}
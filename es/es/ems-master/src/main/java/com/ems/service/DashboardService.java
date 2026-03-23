package com.ems.service;

import com.ems.domain.vo.dashboard.AttendanceStatisticsVO;
import com.ems.domain.vo.dashboard.AttendanceTopVO;
import com.ems.domain.vo.dashboard.AttendanceTrendVO;
import com.ems.domain.vo.dashboard.DeptAttendanceVO;
import com.ems.domain.vo.dashboard.DeptEmpCountVO;
import com.ems.domain.vo.dashboard.MonthlyAttendanceVO;

import java.util.List;

public interface DashboardService {

    /**
     * 获取考勤统计数据
     */
    AttendanceStatisticsVO getAttendanceStatistics();

    /**
     * 获取考勤趋势数据
     * @param days 天数
     */
    List<AttendanceTrendVO> getAttendanceTrend(Integer days);

    /**
     * 获取部门考勤统计
     */
    List<DeptAttendanceVO> getDeptAttendance();

    /**
     * 获取考勤异常TOP数据
     * @param limit 数量限制
     * @param type 类型（1-迟到，2-早退，3-旷工，4-加班）
     */
    List<AttendanceTopVO> getAttendanceTop(Integer limit, Integer type);

    /**
     * 获取部门员工数量统计
     */
    List<DeptEmpCountVO> getDeptEmpCount();

    /**
     * 获取月度考勤统计数据
     * @param year 年份
     * @param month 月份
     */
    MonthlyAttendanceVO getMonthlyAttendance(Integer year, Integer month);

    /**
     * 获取员工数量
     */
    Integer getEmpCount();

    /**
     * 获取请假数量
     */
    Integer getLeaveCount();

    /**
     * 获取考勤异常数量
     */
    Integer getAttendanceCount();
} 
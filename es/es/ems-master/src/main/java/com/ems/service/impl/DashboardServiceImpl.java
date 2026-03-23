package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.domain.pojo.Attendance;
import com.ems.domain.pojo.Emp;
import com.ems.domain.vo.dashboard.AttendanceStatisticsVO;
import com.ems.domain.vo.dashboard.AttendanceTopVO;
import com.ems.domain.vo.dashboard.AttendanceTrendVO;
import com.ems.domain.vo.dashboard.DeptAttendanceVO;
import com.ems.domain.vo.dashboard.DeptEmpCountVO;
import com.ems.domain.vo.dashboard.MonthlyAttendanceVO;
import com.ems.mapper.AttendanceMapper;
import com.ems.mapper.EmpMapper;
import com.ems.mapper.LeaveMapper;
import com.ems.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AttendanceMapper attendanceMapper;
    private final LeaveMapper leaveMapper;
    private final EmpMapper empMapper;

    @Override
    public AttendanceStatisticsVO getAttendanceStatistics() {
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        
        // 获取所有在职员工数量
        LambdaQueryWrapper<Emp> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(Emp::getStatus, 0)  // 状态正常
                .isNull(Emp::getLeaveDate);  // 未离职
        long totalEmps = empMapper.selectCount(empWrapper);
        
        // 获取今日考勤数据
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getAttendanceDate, today);
        List<Attendance> attendances = attendanceMapper.selectList(wrapper);
        
        // 统计各类数据
        int present = (int) attendances.stream()
                .filter(a -> a.getAbsentFlag() == 0 && a.getLeaveFlag() == 0)
                .count();
        
        int late = (int) attendances.stream().filter(a -> a.getLateFlag() == 1).count();
        int early = (int) attendances.stream().filter(a -> a.getEarlyFlag() == 1).count();
        int absent = (int) attendances.stream().filter(a -> a.getAbsentFlag() == 1).count();
        int leave = (int) attendances.stream().filter(a -> a.getLeaveFlag() == 1).count();
        int overtime = (int) attendances.stream().filter(a -> a.getOvertimeFlag() == 1).count();
        
        // 计算时间统计
        int totalLateMinutes = attendances.stream()
                .mapToInt(Attendance::getLateMinutes)
                .sum();
        int totalEarlyMinutes = attendances.stream()
                .mapToInt(Attendance::getEarlyMinutes)
                .sum();
        int totalOvertimeMinutes = attendances.stream()
                .mapToInt(Attendance::getOvertimeMinutes)
                .sum();
        
        // 计算出勤率
        String attendanceRate = String.format("%.1f%%", (present * 100.0) / totalEmps);
        
        // 构建返回对象
        return AttendanceStatisticsVO.builder()
                .total((int) totalEmps)
                .present(present)
                .attendanceRate(attendanceRate)
                .summary(AttendanceStatisticsVO.Summary.builder()
                        .late(late)
                        .early(early)
                        .absent(absent)
                        .leave(leave)
                        .overtime(overtime)
                        .build())
                .timeStats(AttendanceStatisticsVO.TimeStats.builder()
                        .totalLateMinutes(totalLateMinutes)
                        .totalEarlyMinutes(totalEarlyMinutes)
                        .totalOvertimeMinutes(totalOvertimeMinutes)
                        .build())
                .build();
    }

    @Override
    public List<AttendanceTrendVO> getAttendanceTrend(Integer days) {
        // 获取日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 获取日期范围内的考勤数据
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Attendance::getAttendanceDate, startDate, endDate)
                .orderByAsc(Attendance::getAttendanceDate);
        List<Attendance> attendances = attendanceMapper.selectList(wrapper);
        
        // 获取员工总数
        long totalEmps = empMapper.selectCount(null);
        
        // 按日期分组统计
        Map<LocalDate, List<Attendance>> groupByDate = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getAttendanceDate));
        
        // 构建趋势数据
        List<AttendanceTrendVO> trend = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Attendance> dayAttendances = groupByDate.getOrDefault(date, Collections.emptyList());
            
            // 计算出勤率
            int present = (int) dayAttendances.stream()
                    .filter(a -> a.getAbsentFlag() == 0 && a.getLeaveFlag() == 0)
                    .count();
            int attendanceRate = (int) ((present * 100.0) / totalEmps);
            
            // 统计异常数据
            int late = (int) dayAttendances.stream().filter(a -> a.getLateFlag() == 1).count();
            int early = (int) dayAttendances.stream().filter(a -> a.getEarlyFlag() == 1).count();
            int absent = (int) dayAttendances.stream().filter(a -> a.getAbsentFlag() == 1).count();
            int leave = (int) dayAttendances.stream().filter(a -> a.getLeaveFlag() == 1).count();
            int overtime = (int) dayAttendances.stream().filter(a -> a.getOvertimeFlag() == 1).count();
            
            trend.add(AttendanceTrendVO.builder()
                    .date(date)
                    .attendanceRate(attendanceRate)
                    .late(late)
                    .early(early)
                    .absent(absent)
                    .leave(leave)
                    .overtime(overtime)
                    .build());
        }
        
        return trend;
    }

    @Override
    public List<DeptAttendanceVO> getDeptAttendance() {
        try {
            log.info("开始获取部门考勤统计数据");
            
            // 获取今天的日期
            LocalDate today = LocalDate.now();
            log.info("当前统计日期: {}", today);
            
            // 获取各部门员工数量
            List<Map<String, Object>> deptEmpCounts = empMapper.getDeptEmpCount();
            if (deptEmpCounts == null || deptEmpCounts.isEmpty()) {
                log.warn("未获取到部门员工数量数据");
                return Collections.emptyList();
            }
            log.info("获取到 {} 个部门的员工数量数据", deptEmpCounts.size());
            
            List<DeptAttendanceVO> result = new ArrayList<>();
            
            // 遍历每个部门，构建统计数据
            for (Map<String, Object> deptData : deptEmpCounts) {
                String deptName = (String) deptData.get("deptName");
                Long empCount = ((Number) deptData.get("empCount")).longValue();
                
                if (deptName == null || empCount == null) {
                    log.warn("部门数据异常，跳过。deptName: {}, empCount: {}", deptName, empCount);
                    continue;
                }
                
                // 构建部门考勤统计数据
                DeptAttendanceVO deptStats = DeptAttendanceVO.builder()
                        .deptName(deptName)
                        .total(empCount.intValue())
                        .attendanceRate("0.0%")
                        .late(0)
                        .early(0)
                        .absent(0)
                        .leave(0)
                        .overtime(0)
                        .build();
                
                result.add(deptStats);
                log.debug("添加部门统计数据: {}", deptStats);
            }
            
            log.info("部门考勤统计数据获取完成，共 {} 个部门", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("获取部门考勤统计数据时发生错误", e);
            throw new RuntimeException("获取部门考勤统计失败: " + e.getMessage());
        }
    }

    @Override
    public List<AttendanceTopVO> getAttendanceTop(Integer limit, Integer type) {
        // 获取本月考勤数据
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Attendance::getAttendanceDate, startDate, endDate);
        
        // 根据类型添加条件
        switch (type) {
            case 1 -> wrapper.eq(Attendance::getLateFlag, 1);
            case 2 -> wrapper.eq(Attendance::getEarlyFlag, 1);
            case 3 -> wrapper.eq(Attendance::getAbsentFlag, 1);
            case 4 -> wrapper.eq(Attendance::getOvertimeFlag, 1);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
        
        List<Attendance> attendances = attendanceMapper.selectList(wrapper);
        
        // 按员工分组统计
        return attendances.stream()
                .collect(Collectors.groupingBy(
                        a -> new AbstractMap.SimpleEntry<>(a.getEmpId(), a.getEmpName()),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    int count = list.size();
                                    int totalMinutes = switch (type) {
                                        case 1 -> list.stream().mapToInt(Attendance::getLateMinutes).sum();
                                        case 2 -> list.stream().mapToInt(Attendance::getEarlyMinutes).sum();
                                        case 4 -> list.stream().mapToInt(Attendance::getOvertimeMinutes).sum();
                                        default -> 0;
                                    };
                                    
                                    return AttendanceTopVO.builder()
                                            .empId(list.get(0).getEmpId())
                                            .empName(list.get(0).getEmpName())
                                            .deptName(list.get(0).getDeptName())
                                            .count(count)
                                            .totalMinutes(totalMinutes)
                                            .build();
                                }
                        )))
                .values()
                .stream()
                .sorted(Comparator.<AttendanceTopVO>comparingInt(v -> v.getCount()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeptEmpCountVO> getDeptEmpCount() {
        try {
            log.info("开始获取部门员工数量统计");
            
            // 获取各部门员工数量
            List<Map<String, Object>> deptEmpCounts = empMapper.getDeptEmpCount();
            if (deptEmpCounts == null || deptEmpCounts.isEmpty()) {
                log.warn("未获取到部门员工数量数据");
                return Collections.emptyList();
            }
            
            // 转换数据格式
            List<DeptEmpCountVO> result = deptEmpCounts.stream()
                    .map(data -> {
                        String deptName = (String) data.get("deptName");
                        Number empCount = (Number) data.get("empCount");
                        
                        return DeptEmpCountVO.builder()
                                .name(deptName)
                                .value(empCount != null ? empCount.intValue() : 0)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            log.info("部门员工数量统计获取完成，共 {} 个部门", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("获取部门员工数量统计时发生错误", e);
            throw new RuntimeException("获取部门员工数量统计失败: " + e.getMessage());
        }
    }

    @Override
    public MonthlyAttendanceVO getMonthlyAttendance(Integer year, Integer month) {
        try {
            log.info("开始获取{}年{}月考勤统计数据", year, month);
            
            // 获取当月的起止日期
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            
            // 获取员工总数
            LambdaQueryWrapper<Emp> empWrapper = new LambdaQueryWrapper<>();
            empWrapper.eq(Emp::getStatus, 0)  // 状态正常
                    .isNull(Emp::getLeaveDate);  // 未离职
            int totalEmps = Math.toIntExact(empMapper.selectCount(empWrapper));
            
            // 获取当月所有考勤记录
            LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(Attendance::getAttendanceDate, startDate, endDate);
            List<Attendance> allAttendances = attendanceMapper.selectList(wrapper);
            
            // 按日期分组统计
            Map<LocalDate, List<Attendance>> attendanceByDate = allAttendances.stream()
                    .collect(Collectors.groupingBy(Attendance::getAttendanceDate));
            
            // 准备统计数据
            List<String> dates = new ArrayList<>();
            List<Integer> normal = new ArrayList<>();
            List<Integer> late = new ArrayList<>();
            List<Integer> absent = new ArrayList<>();
            
            // 统计每一天的数据
            int totalNormal = 0;
            int totalLate = 0;
            int totalAbsent = 0;
            int workDays = 0;
            
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // 添加日期
                dates.add(date.getDayOfMonth() + "日");
                
                // 获取当天的考勤记录
                List<Attendance> dayAttendances = attendanceByDate.getOrDefault(date, Collections.emptyList());
                
                // 统计当天数据
                int dayNormal = (int) dayAttendances.stream()
                        .filter(a -> a.getLateFlag() == 0 && a.getAbsentFlag() == 0)
                        .count();
                int dayLate = (int) dayAttendances.stream()
                        .filter(a -> a.getLateFlag() == 1)
                        .count();
                int dayAbsent = (int) dayAttendances.stream()
                        .filter(a -> a.getAbsentFlag() == 1)
                        .count();
                
                // 添加到列表
                normal.add(dayNormal);
                late.add(dayLate);
                absent.add(dayAbsent);
                
                // 累计总数
                totalNormal += dayNormal;
                totalLate += dayLate;
                totalAbsent += dayAbsent;
                
                // 计算工作日（这里简单处理，实际可能需要考虑节假日）
                if (date.getDayOfWeek().getValue() <= 5) {
                    workDays++;
                }
            }
            
            // 计算出勤率
            int totalAttendance = totalNormal + totalLate;
            int totalPossibleAttendance = workDays * totalEmps;
            String attendanceRate = String.format("%.1f%%", 
                    totalPossibleAttendance > 0 ? (totalAttendance * 100.0) / totalPossibleAttendance : 0);
            
            // 构建返回对象
            return MonthlyAttendanceVO.builder()
                    .month(month)
                    .total(totalEmps)
                    .statistics(MonthlyAttendanceVO.Statistics.builder()
                            .dates(dates)
                            .normal(normal)
                            .late(late)
                            .absent(absent)
                            .build())
                    .summary(MonthlyAttendanceVO.Summary.builder()
                            .normalDays(workDays)
                            .totalNormal(totalNormal)
                            .totalLate(totalLate)
                            .totalAbsent(totalAbsent)
                            .attendanceRate(attendanceRate)
                            .build())
                    .build();
            
        } catch (Exception e) {
            log.error("获取月度考勤统计数据时发生错误", e);
            throw new RuntimeException("获取月度考勤统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取员工数量
     */
    public Integer getEmpCount() {
        return empMapper.selectEmpCount();
    }

    /**
     * 获取今天请假数量
     */
    public Integer getLeaveCount() {
        return leaveMapper.selectLeaveCount();
    }

    /**
     * 获取考勤异常数量
     */
    public Integer getAttendanceCount() {
        return attendanceMapper.getExceptionCount();
    }
} 
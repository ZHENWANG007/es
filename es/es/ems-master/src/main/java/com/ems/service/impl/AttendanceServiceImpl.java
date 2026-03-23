package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.context.BaseContext;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.attendance.ClockDTO;
import com.ems.domain.dto.page.AttendancePageDTO;
import com.ems.domain.pojo.Attendance;
import com.ems.domain.vo.attendance.AttendanceCalendarVO;
import com.ems.mapper.AttendanceMapper;
import com.ems.mapper.DeptMapper;
import com.ems.mapper.EmpMapper;
import com.ems.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final DeptMapper deptMapper;
    private final EmpMapper empMapper;

    /**
     * 分页查询
     * @param attendancePageDTO 分页条件
     * @return 分页结果
     */
    public PageResult pageQuery(AttendancePageDTO attendancePageDTO) {
        // 分页条件
        String deptName = deptMapper.getNameById(attendancePageDTO.getDeptId());
        // 考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假）
        Integer status = attendancePageDTO.getStatus();

        Page<Attendance> page = Page.of(attendancePageDTO.getPageNum(), attendancePageDTO.getPageSize());

        // 构建查询条件
        Page<Attendance> p = lambdaQuery()
                .like(StringUtils.isNotBlank(attendancePageDTO.getEmpName()), Attendance::getEmpName, attendancePageDTO.getEmpName())
                .eq(StringUtils.isNotBlank(deptName), Attendance::getDeptName, deptName)
                .eq(attendancePageDTO.getAttendanceDate() != null, Attendance::getAttendanceDate, attendancePageDTO.getAttendanceDate())
                // 根据不同状态添加对应的查询条件
                .and(status != null, wrapper -> {
                    switch (status) {
                        case 0 -> wrapper
                                .eq(Attendance::getLateFlag, 0)
                                .eq(Attendance::getEarlyFlag, 0)
                                .eq(Attendance::getAbsentFlag, 0)
                                .eq(Attendance::getLeaveFlag, 0);
                        case 1 -> wrapper.eq(Attendance::getLateFlag, 1);
                        case 2 -> wrapper.eq(Attendance::getEarlyFlag, 1);
                        case 3 -> wrapper.eq(Attendance::getAbsentFlag, 1);
                        case 4 -> wrapper.eq(Attendance::getLeaveFlag, 1);
                    }
                })
                .orderByDesc(Attendance::getAttendanceDate)
                .orderByDesc(Attendance::getCreateTime)
                .page(page);


        return PageResult.builder()
                .total(p.getTotal())
                .records(p.getRecords())
                .build();
    }

    /**
     * 打卡
     * @param clockDTO 打卡信息
     */
    public void clock(ClockDTO clockDTO) {
        // 获取当前登录员工ID
        Long empId = BaseContext.getCurrentId();

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // 查询今日考勤记录
        Attendance attendance = attendanceMapper.getByEmpIdAndDate(empId, today);

        // 如果没有记录，创建新记录
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmpId(empId);
            attendance.setEmpName(empMapper.getNameById(empId));
            attendance.setDeptName(deptMapper.getNameByEmpId(empId));
            attendance.setAttendanceDate(today);
            // 设置默认值
            attendance.setLateFlag(0);
            attendance.setEarlyFlag(0);
            attendance.setAbsentFlag(0);
            attendance.setLeaveFlag(0);
            attendance.setOvertimeFlag(0);
            attendance.setLateMinutes(0);
            attendance.setEarlyMinutes(0);
            attendance.setOvertimeMinutes(0);
        }

        // 判断打卡类型
        if ("IN".equals(clockDTO.getType())) {
            // 上班打卡 - 取最早打卡时间
            if (attendance.getCheckInTime() == null || now.isBefore(attendance.getCheckInTime())) {
                attendance.setCheckInTime(now);
                // 判断是否迟到（上班打卡时间为9点）
                LocalDateTime standardTime = LocalDateTime.of(today, LocalTime.of(9, 0));
                if (now.isAfter(standardTime)) {
                    attendance.setLateFlag(1);
                    // 计算迟到分钟数
                    attendance.setLateMinutes((int) Duration.between(standardTime, now).toMinutes());
                    attendance.setRemark(generateAttendanceRemark(now, standardTime, "LATE"));
                } else {
                    // 如果新的打卡时间不迟到，重置状态
                    attendance.setLateFlag(0);
                    attendance.setLateMinutes(0);
                    attendance.setRemark(null);
                }
            }
        } else {
            // 下班打卡 - 取最晚打卡时间
            if (attendance.getCheckOutTime() == null || now.isAfter(attendance.getCheckOutTime())) {
                attendance.setCheckOutTime(now);
                LocalDateTime standardTime = LocalDateTime.of(today, LocalTime.of(18, 0));
                
                // 判断是否早退（下班时间为18点）
                if (now.isBefore(standardTime)) {
                    attendance.setEarlyFlag(1);
                    // 计算早退分钟数
                    attendance.setEarlyMinutes((int) Duration.between(now, standardTime).toMinutes());
                    attendance.setRemark(generateAttendanceRemark(now, standardTime, "EARLY"));
                } else {
                    attendance.setEarlyFlag(0);
                    attendance.setEarlyMinutes(0);
                    
                    // 判断是否加班（19点后算加班）
                    LocalDateTime overtimeTime = LocalDateTime.of(today, LocalTime.of(19, 0));
                    if (now.isAfter(overtimeTime)) {
                        attendance.setOvertimeFlag(1);
                        // 计算加班分钟数
                        attendance.setOvertimeMinutes((int) Duration.between(overtimeTime, now).toMinutes());
                        attendance.setRemark(generateAttendanceRemark(now, overtimeTime, "OVERTIME"));
                    } else {
                        attendance.setOvertimeFlag(0);
                        attendance.setOvertimeMinutes(0);
                        // 如果不是迟到也不是早退，清除备注
                        if (attendance.getLateFlag() == 0) {
                            attendance.setRemark(null);
                        }
                    }
                }
            }
        }

        // 保存打卡记录
        saveOrUpdate(attendance);
    }

    /**
     * 查询历史打卡记录
     * @param yearMonth 年月 2023-10
     * @return 分页结果
     */
    public List<AttendanceCalendarVO> monthHistory(String yearMonth) {
        // 获取当前登录员工ID
        Long empId = BaseContext.getCurrentId();

        // 解析年月
        LocalDate date = LocalDate.parse(yearMonth + "-01");
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());

        // 查询当月所有打卡记录
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmpId, empId)
                .between(Attendance::getAttendanceDate, firstDay, lastDay)
                .orderByAsc(Attendance::getAttendanceDate);

        List<Attendance> list = attendanceMapper.selectList(wrapper);

        // 转换为日历展示数据
        List<AttendanceCalendarVO> result = new ArrayList<>();
        // 将列表转换为字典，键为出勤日期，值为出勤记录，以便快速根据日期查找出勤信息
        Map<LocalDate, Attendance> attendanceMap = list.stream()
                .collect(Collectors.toMap(Attendance::getAttendanceDate, a -> a));

        // 遍历当月每一天
        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            AttendanceCalendarVO vo = new AttendanceCalendarVO();
            vo.setDate(current.toString());

            Attendance attendance = attendanceMap.get(current);
            if (attendance != null) {
                // 获取所有异常状态
                List<Integer> states = new ArrayList<>();
                if (attendance.getLateFlag() == 1) states.add(1);      // 迟到
                if (attendance.getEarlyFlag() == 1) states.add(2);     // 早退
                if (attendance.getAbsentFlag() == 1) states.add(3);    // 旷工
                if (attendance.getLeaveFlag() == 1) states.add(4);     // 请假
                
                // 如果没有任何异常状态，则为正常
                if (states.isEmpty()) {
                    vo.setStatus("0");  // 正常
                } else {
                    // 将多个状态组合成字符串，用逗号分隔
                    String combinedStatus = states.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    vo.setStatus(combinedStatus);
                }

                // 设置打卡时间，使用HH:mm格式
                if (attendance.getCheckInTime() != null) {
                    vo.setCheckInTime(attendance.getCheckInTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                } else {
                    vo.setCheckInTime("--:--");
                }
                if (attendance.getCheckOutTime() != null) {
                    vo.setCheckOutTime(attendance.getCheckOutTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                } else {
                    vo.setCheckOutTime("--:--");
                }
            } else {
                vo.setStatus("3");
                vo.setCheckInTime("--:--");
                vo.setCheckOutTime("--:--");
            }

            result.add(vo);
            current = current.plusDays(1);
        }

        return result;
    }

    /**
     * 查询我的打卡记录
     * 考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假
     */
    public Map<Integer, Integer> myAttendanceCount() {
        Long currentEmpId = BaseContext.getCurrentId();
        Map<Integer, Integer> countMap = new HashMap<>();

        // 初始化所有状态的计数为0
        countMap.put(0, 0); // 正常
        countMap.put(1, 0); // 迟到
        countMap.put(2, 0); // 早退
        countMap.put(3, 0); // 旷工
        countMap.put(4, 0); // 请假

        // 查询当前员工的所有考勤记录
        List<Attendance> attendanceList = lambdaQuery()
                .eq(Attendance::getEmpId, currentEmpId)
                .list();

        // 统计各种状态的数量
        for (Attendance attendance : attendanceList) {
            if (attendance.getLateFlag() == 1) {
                countMap.put(1, countMap.get(1) + 1);
            } else if (attendance.getEarlyFlag() == 1) {
                countMap.put(2, countMap.get(2) + 1);
            } else if (attendance.getAbsentFlag() == 1) {
                countMap.put(3, countMap.get(3) + 1);
            } else if (attendance.getLeaveFlag() == 1) {
                countMap.put(4, countMap.get(4) + 1);
            } else {
                // 如果没有任何异常标记，则为正常考勤
                countMap.put(0, countMap.get(0) + 1);
            }
        }

        return countMap;
    }

    /**
     * 查询我的上下班打卡状态
     */
    public Map<String, Integer> myAttendanceToday() {
        Long currentEmpId = BaseContext.getCurrentId();
        
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        
        // 查询今天的考勤记录
        Attendance attendance = attendanceMapper.getByEmpIdAndDate(currentEmpId, today);
        
        Map<String, Integer> result = new HashMap<>();
        
        // 初始化打卡状态,0表示未打卡,1表示已打卡
        result.put("IN", 0);  // 上班打卡
        result.put("OUT", 0); // 下班打卡
        
        if (attendance != null) {
            // 检查上班打卡
            if (attendance.getCheckInTime() != null) {
                result.put("IN", 1);
            }
            
            // 检查下班打卡
            if (attendance.getCheckOutTime() != null) {
                result.put("OUT", 1);
            }
        }
        
        return result;
    }

    /**
     * 设置考勤备注信息
     * @param actualTime 实际打卡时间
     * @param standardTime 标准时间
     * @param type 打卡类型：迟到、早退、加班
     * @return 备注信息
     */
    private String generateAttendanceRemark(LocalDateTime actualTime, LocalDateTime standardTime, String type) {
        Duration duration = Duration.between(standardTime, actualTime);
        long hours = Math.abs(duration.toHours());
        long minutes = Math.abs(duration.toMinutes() % 60);

        StringBuilder remark = new StringBuilder();
        switch (type) {
            case "LATE" -> {
                remark.append("迟到");
                if (hours > 0) {
                    remark.append(hours).append("小时");
                }
                if (minutes > 0) {
                    remark.append(minutes).append("分钟");
                }
            }
            case "EARLY" -> {
                remark.append("早退");
                if (hours > 0) {
                    remark.append(hours).append("小时");
                }
                if (minutes > 0) {
                    remark.append(minutes).append("分钟");
                }
            }
            case "OVERTIME" -> {
                remark.append("加班");
                if (hours > 0) {
                    remark.append(hours).append("小时");
                }
                if (minutes > 0) {
                    remark.append(minutes).append("分钟");
                }
            }
        }
        return remark.toString();
    }
}
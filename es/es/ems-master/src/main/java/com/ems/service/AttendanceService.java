package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.attendance.ClockDTO;
import com.ems.domain.dto.page.AttendancePageDTO;
import com.ems.domain.pojo.Attendance;
import com.ems.domain.vo.attendance.AttendanceCalendarVO;

import java.util.List;
import java.util.Map;


public interface AttendanceService extends IService<Attendance> {

    /**
     * 分页查询
     * @param attendancePageDTO 分页条件
     * @return 分页结果
     */
    PageResult pageQuery(AttendancePageDTO attendancePageDTO);

    /**
     * 打卡
     * @param clockDTO 打卡信息
     */
    void clock(ClockDTO clockDTO);


    /**
     * 查询历史打卡记录
     * @param yearMonth 年月
     * @return 分页结果
     */
    List<AttendanceCalendarVO> monthHistory(String yearMonth);

    /**
     * 查询我的打卡记录
     * 考勤状态（0-正常，1-迟到，2-早退，3-旷工，4-请假
     */
    Map<Integer, Integer> myAttendanceCount();

    /**
     * 查询我的上下班打卡状态
     */
    Map<String, Integer> myAttendanceToday();
}

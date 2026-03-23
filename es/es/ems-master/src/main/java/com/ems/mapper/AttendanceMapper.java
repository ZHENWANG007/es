package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.pojo.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {

    @Select("select * from attendance where emp_id = #{empId} and attendance_date = #{today}")
    Attendance getByEmpIdAndDate(Long empId, LocalDate today);

    Integer getExceptionCount();
}

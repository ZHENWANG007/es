package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.pojo.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LeaveMapper extends BaseMapper<LeaveRequest> {

    /**
     * 查询今天请假记录总数
     */
    @Select("select count(*) from leave_request where date(end_date)>=date(now()) and overall_status = 1")
    Integer selectLeaveCount();
}
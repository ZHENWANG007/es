package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.pojo.Job;
import com.ems.domain.vo.Job.JobNameVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JobMapper extends BaseMapper<Job> {

    @Select("select id, name from job where status = 0")
    List<JobNameVO> selectJobName();

    Integer hasEmp(List<Long> ids);
}

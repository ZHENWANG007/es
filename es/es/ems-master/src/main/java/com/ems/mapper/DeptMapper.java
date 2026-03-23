package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.pojo.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    @Select("select name from dept where id = #{deptId}")
    String getNameById(String deptId);

    String getNameByEmpId(Long empId);

    @Select("select id from dept where parent_id = #{id}")
    List<Long> getChildDeptIds(Long id);

    @Select("select id from dept where name = #{deptName}")
    Long getIdByName(String deptName);
}

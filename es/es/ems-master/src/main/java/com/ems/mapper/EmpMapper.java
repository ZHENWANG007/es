package com.ems.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.pojo.Emp;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapper extends BaseMapper<Emp> {

    void addEmpRole(List<Long> empIds);

    @Delete("delete from emp where dept_id = #{deptId}")
    void deleteEmpByDeptId(Long deptId);

    void deleteEmpRole(List<Long> empIds);

    void updateEmpStatusByDeptIds(List<Long> deptIds, Integer status);

    @Select("select name from emp where id = #{leaderApprovedBy}")
    String getNameById(Long leaderApprovedBy);

    @Select("select id from emp where dept_id = #{deptId}")
    List<Long> getEmpIdsByDeptId(Long deptId);

    @Update("update emp_role set role_id = #{roleId} where emp_id = #{empId}")
    void updateEmpRole(Long empId, Long roleId);

    /**
     * 获取各部门员工数量
     * @return 部门名称和员工数量的映射
     */
    @Select("SELECT d.name as deptName, COUNT(e.id) as empCount " +
            "FROM dept d " +
            "LEFT JOIN emp e ON d.id = e.dept_id " +
            "WHERE e.status = 0 AND e.leave_date IS NULL " +
            "GROUP BY d.id, d.name")
    List<Map<String, Object>> getDeptEmpCount();

    /**
     * 获取员工数量
     * @return 员工数量
     */
    @Select("SELECT COUNT(*) FROM emp WHERE status = 0 AND leave_date IS NULL")
    Integer selectEmpCount();
}

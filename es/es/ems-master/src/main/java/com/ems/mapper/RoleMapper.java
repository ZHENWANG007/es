package com.ems.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.dto.role.RoleMenusAddDTO;
import com.ems.domain.pojo.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Delete("delete from role_menu where role_id = #{id}")
    void deleteRoleMenuByRoleId(Long id);

    void deleteRoleMenuByRoleIds(List<Long> ids);

    @Select("select menu_id from role_menu where role_id = #{id}")
    List<Integer> getRoleMenus(Long id);

    void addRoleMenus(RoleMenusAddDTO roleMenusAddDTO);

    @Insert("insert into role_menu(role_id, menu_id) values(#{roleId}, #{menuId})")
    void addRoleMenu(Long roleId, Long menuId);

    @Select("select role_id from emp_role where emp_id = #{empId}")
    Long getRoleIdByEmpId(Long empId);

    String getRoleKeyByEmpId(Long empId);

}

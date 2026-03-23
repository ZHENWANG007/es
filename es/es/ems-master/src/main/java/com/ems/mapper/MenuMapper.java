package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.domain.pojo.Menu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT t1.perms FROM menu t1\n" +
            "     INNER JOIN role_menu t2 ON t2.menu_id = t1.id\n" +
            "     INNER JOIN role t3 ON t3.id = t2.role_id\n" +
            "     INNER JOIN emp_role t4 ON t4.role_id = t3.id\n" +
            "     INNER JOIN emp t5 ON t5.id = t4.emp_id\n" +
            "WHERE t5.id = #{id} AND t1.perms IS NOT NULL And t5.status = 0 And t3.status = 0 And t1.status = 0;")
    List<String> getMenuByUserId(Long id);

    /**
     * 根据员工ID查询菜单列表
     */
    @Select("SELECT DISTINCT m.* FROM menu m " +
            "LEFT JOIN role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} " +
            "AND m.type = 'M' " +
            "AND m.status = 0 " +
            "ORDER BY m.parent_id")
    List<Menu> selectMenusByRoleId(Long roleId);

    /**
     * 根据员工ID查询菜单列表
     */
    @Select("SELECT DISTINCT m.* FROM menu m " +
            "LEFT JOIN role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} " +
            "AND m.status = 0 " +
            "ORDER BY m.parent_id")
    List<Menu> selectMenusByRoleId2(Long roleId);

    /**
     * 根据员工ID查询正常按钮权限标识列表
     */
    @Select("SELECT DISTINCT m.perms FROM menu m " +
            "LEFT JOIN role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} " +
            "AND m.type = 'B' " +
            "AND m.status = 0 ")
    List<String> selectPermsByRoleId(Long roleId);

    /**
     * 根据菜单ID删除角色菜单关联数据
     */
    @Delete("DELETE FROM role_menu WHERE menu_id = #{id}")
    void deleteRoleMenuByMenuId(Long id);
}

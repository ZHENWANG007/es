package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.domain.dto.menu.MenuAddDTO;
import com.ems.domain.pojo.Menu;
import com.ems.domain.vo.menu.RouterVO;

import java.util.List;


public interface MenuService extends IService<Menu> {

    /**
     * 根据角色ID获取路由列表
     * @param roleId 角色ID
     * @return 路由列表
     */
    List<RouterVO> getRoutersByRoleId(Long roleId);

    /**
     * 获取角色按钮权限列表
     * @param roleId 角色ID
     * @return 权限标识列表
     */
    List<String> getPermissionsByRoleId(Long roleId);

    /**
     * 根据菜单ID删除菜单以及子菜单以及角色菜单关联表中的记录
     * @param id 菜单ID
     */
    void removeMenuById(Long id);

    /**
     * 根据角色ID获取菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<RouterVO> getMenusByRoleId(Long roleId);

    /**
     * 新增菜单
     * @param menuAddDTO 菜单添加/修改DTO
     */
    void addMenu(MenuAddDTO menuAddDTO);

    /**
     * 修改菜单
     * @param menuAddDTO 菜单添加/修改DTO
     */
    void updateMenu(MenuAddDTO menuAddDTO);
}

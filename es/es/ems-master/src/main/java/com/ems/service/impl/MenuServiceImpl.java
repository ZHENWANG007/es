package com.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.domain.dto.menu.MenuAddDTO;
import com.ems.domain.dto.role.RoleMenusAddDTO;
import com.ems.domain.pojo.Menu;
import com.ems.domain.vo.menu.RouterVO;
import com.ems.mapper.MenuMapper;
import com.ems.mapper.RoleMapper;
import com.ems.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final MenuMapper menuMapper;
    private final RoleMapper roleMapper;

    /**
     * 根据角色ID获取路由菜单
     * @param roleId 角色ID
     * @return 路由菜单列表
     */
    public List<RouterVO> getRoutersByRoleId(Long roleId) {
        List<Menu> menus;
        // 如果是超级管理员，获取所有正常菜单
        if (roleId == 1L) {
            menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getType, 'M') // 只获取菜单,不获取按钮
                    .eq(Menu::getStatus, 0) // 只查询正常状态的菜单
                    .orderByAsc(Menu::getParentId));
        } else {
            // 其他用户根据角色获取菜单
            menus = menuMapper.selectMenusByRoleId(roleId);
        }

        // 构建路由树
        List<RouterVO> routerVOList = buildRouterTree(menus, 0L);

        // 对外层菜单进行排序
        Collections.sort(routerVOList, Comparator.comparingInt(RouterVO::getOrderNum));

        // 对每个菜单的子菜单进行排序
        for (RouterVO routerVO : routerVOList) {
            if (routerVO.getChildren() != null && !routerVO.getChildren().isEmpty()) {
                Collections.sort(routerVO.getChildren(), Comparator.comparingInt(RouterVO::getOrderNum));
            }
        }

        return routerVOList;
    }

    /**
     * 根据角色ID获取菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    public List<RouterVO> getMenusByRoleId(Long roleId) {
        List<Menu> menus;
        // 是超级管理员，获取所有正常菜单
        menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 0) // 只查询正常状态的菜单
                .orderByAsc(Menu::getParentId));

        // 构建路由树
        List<RouterVO> routerVOList = buildRouterTree(menus, 0L);

        // 对外层菜单进行排序
        Collections.sort(routerVOList, Comparator.comparingInt(RouterVO::getOrderNum));

        // 对每个菜单的子菜单进行排序
        for (RouterVO routerVO : routerVOList) {
            if (routerVO.getChildren() != null && !routerVO.getChildren().isEmpty()) {
                Collections.sort(routerVO.getChildren(), Comparator.comparingInt((RouterVO vo) -> Optional.ofNullable(vo.getOrderNum()).orElse(Integer.MAX_VALUE)));

            }
        }

        return routerVOList;
    }

    /**
     * 新增菜单
     * @param menuAddDTO 菜单添加/修改DTO
     */
    @Transactional
    public void addMenu(MenuAddDTO menuAddDTO) {

        // 1. 校验菜单名称是否重复
        long count = count(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuName, menuAddDTO.getMenuName()));
        if (count > 0) {
            throw new RuntimeException("菜单名称已存在"); //BusinessException
        }

        // 2. 如果有父菜单，检查父菜单是否存在
        if (menuAddDTO.getParentId() != null && menuAddDTO.getParentId() != 0) {
            Menu parentMenu = getById(menuAddDTO.getParentId());
            if (parentMenu == null) {
                throw new RuntimeException("父菜单不存在");
            }
        }

        // 3. 保存菜单信息
        Menu menu = BeanUtil.copyProperties(menuAddDTO, Menu.class);
        save(menu);
        // 4. 给超级管理员添加菜单权限
        roleMapper.addRoleMenu(1L, menu.getId());
    }

    /**
     * 修改菜单
     * @param menuAddDTO 菜单添加/修改DTO
     */
    public void updateMenu(MenuAddDTO menuAddDTO) {

        // 1. 检查菜单是否存在
        Menu existMenu = getById(menuAddDTO.getId());
        if (existMenu == null) {
            throw new RuntimeException("菜单不存在");
        }

        // 2. 检查菜单名称是否重复
        long count = count(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuName, menuAddDTO.getMenuName())
                .ne(Menu::getId, menuAddDTO.getId())); // 排除当前菜单
        if (count > 0) {
            throw new RuntimeException("菜单名称已存在");
        }

        // 3. 如果有父菜单，检查父菜单是否存在
        if (menuAddDTO.getParentId() != null && menuAddDTO.getParentId() != 0) {

            // 不能将部门的父部门设置为自己或自己的子部门
            if (menuAddDTO.getParentId().equals(menuAddDTO.getId())) {
                throw new RuntimeException("不能将部门的父部门设置为自己或自己的子部门");
            }

            Menu parentMenu = getById(menuAddDTO.getParentId());

            if (parentMenu == null) {
                throw new RuntimeException("父菜单不存在");
            }
        }

        // 4. 更新菜单信息
        updateById(BeanUtil.copyProperties(menuAddDTO, Menu.class));


    }


    /**
     * 获取角色按钮权限列表
     * @param roleId 角色ID
     * @return 权限标识列表
     */
    public List<String> getPermissionsByRoleId(Long roleId) {
        // 超级管理员拥有所有正常按钮权限
        if (roleId == 1L) {
            return menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getType, 'B') // 只获取按钮,不获取菜单
                    .eq(Menu::getStatus, 0)) // 只查询正常状态的按钮
                    .stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
        }
        // 其他用户根据角色获取按钮权限
        return menuMapper.selectPermsByRoleId(roleId);
    }

    /**
     * 根据菜单ID删除菜单以及子菜单以及角色菜单关联表中的记录
     * @param id 菜单ID
     */
    @Transactional
    public void removeMenuById(Long id) {
        // 删除菜单
        menuMapper.deleteById(id);
        // 删除子菜单
        menuMapper.delete(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        // 删除角色菜单关联表中的记录
        menuMapper.deleteRoleMenuByMenuId(id);
    }


    /**
     * 构建路由树形结构
     * @param menus 菜单列表
     * @param parentId 父菜单ID
     * @return 树形结构的路由列表
     */
    private List<RouterVO> buildRouterTree(List<Menu> menus, Long parentId) {
        List<RouterVO> routers = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                RouterVO router = new RouterVO();
                router.setId(menu.getId());
                router.setParentId(menu.getParentId());
                router.setMenuName(menu.getMenuName());
                router.setPath(menu.getPath());
                router.setComponent(menu.getComponent());
                router.setPerms(menu.getPerms());
                router.setIcon(menu.getIcon());
                router.setOrderNum(menu.getOrderNum());
                router.setVisible(menu.getVisible());
                router.setStatus(menu.getStatus());
                router.setType(menu.getType());
                
                // 递归获取子菜单
                List<RouterVO> children = buildRouterTree(menus, menu.getId());
                router.setChildren(children);
                routers.add(router);
            }
        }
        return routers;
    }



}
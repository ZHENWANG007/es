package com.ems.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.page.RolesPageDTO;
import com.ems.domain.dto.role.RoleMenusAddDTO;
import com.ems.domain.pojo.Role;
import com.ems.domain.vo.role.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色
     * @param rolesPageDTO 分页参数
     * @return 分页结果
     */
    PageResult pageQuery(RolesPageDTO rolesPageDTO);

    /**
     * 根据角色ID删除角色和分配的权限
     * @param ids 角色ID列表
     */
    void removeRoleById(List<Long> ids);

    /**
     * 根据角色ID查询角色拥有的菜单权限ID
     * @param id 角色ID
     * @return 权限ID列表
     */
    List<Integer> getRoleMenus(Long id);

    /**
     * 更新角色和分配的权限
     * @param roleMenusAddDTO 角色和权限信息
     */
    void addRoleMenus(RoleMenusAddDTO roleMenusAddDTO);

    /**
     * 查询角色列表
     * @return 角色列表
     */
    List<RoleVO> getList();
}

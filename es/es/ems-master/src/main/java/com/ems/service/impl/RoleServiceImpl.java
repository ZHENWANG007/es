package com.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.page.RolesPageDTO;
import com.ems.domain.dto.role.RoleAddDTO;
import com.ems.domain.dto.role.RoleMenusAddDTO;
import com.ems.domain.pojo.Role;
import com.ems.domain.vo.role.RoleVO;
import com.ems.mapper.MenuMapper;
import com.ems.mapper.RoleMapper;
import com.ems.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    /**
     * 分页查询角色
     * @param rolesPageDTO 分页参数
     * @return 分页结果
     */
    public PageResult pageQuery(RolesPageDTO rolesPageDTO) {

        // 分页条件
        Page<Role> page = Page.of(rolesPageDTO.getPageNum(), rolesPageDTO.getPageSize());

        Page<Role> p = lambdaQuery()
                .like(StringUtils.isNotBlank(rolesPageDTO.getRoleName()), Role::getName, rolesPageDTO.getRoleName())
                .like(StringUtils.isNotBlank(rolesPageDTO.getRoleKey()), Role::getRoleKey, rolesPageDTO.getRoleKey())
                .eq(rolesPageDTO.getStatus() != null, Role::getStatus, rolesPageDTO.getStatus())
                .page(page);

        return PageResult.builder()
                .total(p.getTotal())
                .records(p.getRecords())
                .build();
    }

    /**
     * 根据角色ID删除角色和分配的权限
     * @param ids 角色ID列表
     */
    @Transactional
    public void removeRoleById(List<Long> ids) {
        roleMapper.deleteBatchIds(ids);
        roleMapper.deleteRoleMenuByRoleIds(ids);
    }

    /**
     * 根据角色ID查询角色拥有的菜单权限ID
     * @param id 角色ID
     * @return 权限ID列表
     */
    public List<Integer> getRoleMenus(Long id) {
        return roleMapper.getRoleMenus(id);
    }

    /**
     * 更新角色和分配的权限
     * @param roleMenusAddDTO 角色和权限信息
     */
    @Transactional
    public void addRoleMenus(RoleMenusAddDTO roleMenusAddDTO) {
        // 删除角色菜单关联表中与该角色关联的记录
        roleMapper.deleteRoleMenuByRoleId(roleMenusAddDTO.getRoleId());
        // 重新插入角色和菜单的关联关系
        roleMapper.addRoleMenus(roleMenusAddDTO);

    }

    /**
     * 查询角色列表
     * @return 角色列表
     */
    public List<RoleVO> getList() {
        return roleMapper.selectList(null)
                .stream()
                .map(role -> BeanUtil.copyProperties(role, RoleVO.class))
                .toList();
    }


}
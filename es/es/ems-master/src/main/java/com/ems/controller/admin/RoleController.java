package com.ems.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.ems.common.result.PageResult;
import com.ems.common.result.Result;
import com.ems.domain.dto.page.RolesPageDTO;
import com.ems.domain.dto.role.RoleAddDTO;
import com.ems.domain.dto.role.RoleMenusAddDTO;
import com.ems.domain.pojo.Role;
import com.ems.domain.vo.role.RoleVO;
import com.ems.service.EmpService;
import com.ems.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("admin/role")
@Tag(name = "权限-角色管理接口")
@Slf4j
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/page")
    @Operation(summary = "角色分页查询")
    @PreAuthorize("hasAnyAuthority('ems:role:list')")
    public Result<PageResult> getRoles(RolesPageDTO rolesPageDTO) {
        log.info("角色分页查询，参数{}", rolesPageDTO);
        PageResult pageResult = roleService.pageQuery(rolesPageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "查询角色列表")
    @PreAuthorize("hasAnyAuthority('ems:role:list')")
    public Result<List<RoleVO>> getRoleList() {
        log.info("查询角色列表");
        return Result.success(roleService.getList());
    }

    @PostMapping
    @Operation(summary = "新增角色")
    @PreAuthorize("hasAnyAuthority('ems:role:add')")
    public Result insertRole(@RequestBody RoleAddDTO roleAddDTO) {
        log.info("角色新增，参数{}", roleAddDTO);
        Role role = BeanUtil.copyProperties(roleAddDTO, Role.class);
        roleService.save(role);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改角色")
    @PreAuthorize("hasAnyAuthority('ems:role:edit')")
    public Result updateRole(@RequestBody RoleAddDTO roleAddDTO) {
        log.info("角色修改，参数{}", roleAddDTO);
        Role role = BeanUtil.copyProperties(roleAddDTO, Role.class);
        roleService.updateById(role);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "批量删除角色")
    @PreAuthorize("hasAnyAuthority('ems:role:delete')")
    public Result deleteRole(@RequestBody List<Long> ids) {
        log.info("角色删除，参数{}", ids);
        roleService.removeRoleById(ids);
        return Result.success();
    }

    @GetMapping("/menus/{id}")
    @Operation(summary = "查询角色的菜单权限")
    @PreAuthorize("hasAnyAuthority('ems:menu:list')")
    public Result<List<Integer>> getRoleMenus(@PathVariable("id") Long id) {
        log.info("角色查询的菜单权限列表，参数{}", id);
        List<Integer> menus = roleService.getRoleMenus(id);
        return Result.success(menus);
    }

    @PostMapping("/menus")
    @Operation(summary = "分配角色菜单权限")
    @PreAuthorize("hasAnyAuthority('ems:menu:add')")
    public Result updateRoleMenus(@RequestBody RoleMenusAddDTO roleMenusAddDTO) {
        log.info("角色分配菜单权限，参数{}", roleMenusAddDTO);
        roleService.addRoleMenus(roleMenusAddDTO);
        return Result.success();
    }

}
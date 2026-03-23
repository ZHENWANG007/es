package com.ems.controller.admin;

import com.ems.common.result.Result;
import com.ems.domain.dto.menu.MenuAddDTO;
import com.ems.domain.vo.menu.RouterVO;
import com.ems.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("admin/menu")
@Tag(name = "菜单管理接口")
@Slf4j
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取菜单和按钮
     * @return 统一返回结果
     */
    @GetMapping("/allMenus")
    @Operation(summary = "获取菜单和按钮")
    @PreAuthorize("hasAuthority('ems:menu:list')")
    @Cacheable(cacheNames = "menusTree",key = "#root.methodName") // 缓存菜单树形结构
    public Result<List<RouterVO>> allMenus() {
        log.info("获取菜单和按钮");
        List<RouterVO> menuTree = menuService.getMenusByRoleId(1L);
        return Result.success(menuTree);
    }

    @PostMapping
    @Operation(summary = "新增菜单")
    @PreAuthorize("hasAuthority('ems:menu:add')")
    @CacheEvict(value = "menusTree", allEntries = true) // 删除菜单树形结构缓存
    public Result addMenu(@RequestBody MenuAddDTO menuAddDTO) {
        log.info("新增菜单，参数：{}", menuAddDTO);
        menuService.addMenu(menuAddDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改菜单")
    @PreAuthorize("hasAuthority('ems:menu:edit')")
    @CacheEvict(value = "menusTree", allEntries = true) // 删除菜单树形结构缓存
    public Result updateMenu(@RequestBody MenuAddDTO menuAddDTO) {
        log.info("修改菜单，参数：{}", menuAddDTO);
        menuService.updateMenu(menuAddDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @PreAuthorize("hasAuthority('ems:menu:delete')")
    @CacheEvict(value = "menusTree", allEntries = true)// 删除菜单树形结构缓存
    public Result deleteMenu(@PathVariable("id") Long id) {
        log.info("删除菜单，参数：{}", id);
        menuService.removeMenuById(id);
        return Result.success();
    }


}
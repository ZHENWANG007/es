package com.ems.controller.admin;

import com.ems.common.result.Result;
import com.ems.domain.dto.dept.DeptAddDTO;
import com.ems.domain.vo.dept.DeptTreeVO;
import com.ems.domain.vo.dept.DeptVO;
import com.ems.service.DeptService;
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
@RequestMapping("admin/dept")
@Tag(name = "部门管理接口")
@Slf4j
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping("/tree")
    @Operation(summary = "获取部门树形结构")
    @PreAuthorize("hasAuthority('ems:dept:list')")
    @Cacheable(cacheNames = "deptTree", key = "'deptTree'") // 缓存部门树形结构
    public Result<List<DeptTreeVO>> getDeptTree() {
        log.info("获取部门树形结构");
        List<DeptTreeVO> deptTree = deptService.getDeptTree("TREE");
        return Result.success(deptTree);
    }

    @GetMapping("/list")
    @Operation(summary = "获取部门树形列表")
    @PreAuthorize("hasAuthority('ems:dept:list')")
    @Cacheable(cacheNames = "deptTree", key = "'deptList'") // 缓存部门树列表结构
    public Result<List<DeptTreeVO>> getDeptList() {
        log.info("获取部门树形列表");
        List<DeptTreeVO> deptTree = deptService.getDeptTree("LIST");
        return Result.success(deptTree);
    }

    @GetMapping("/hasEmp/{id}")
    @Operation(summary = "获取当前部门是否有成员")
    @PreAuthorize("hasAuthority('ems:dept:list')")
    public Result<Boolean> hasEmp(@PathVariable("id") Long id) {
        log.info("获取当前部门是否有成员，部门ID：{}", id);
        boolean hasEmp = deptService.hasEmp(id);
        return Result.success(hasEmp);
    }


    @GetMapping("/{id}")
    @Operation(summary = "获取部门详情")
    @PreAuthorize("hasAuthority('ems:dept:list')")
    public Result<DeptVO> getDeptDetail(@PathVariable("id") Long id) {
        log.info("获取部门详情，部门ID：{}", id);
        DeptVO deptVO = deptService.getDeptDetail(id);
        return Result.success(deptVO);
    }



    @PostMapping
    @Operation(summary = "新增部门")
    @PreAuthorize("hasAuthority('ems:dept:add')")
    @CacheEvict(cacheNames = "deptTree", allEntries = true) // 删除部门树形结构缓存
    public Result addDept(@RequestBody DeptAddDTO deptDTO) {
        log.info("新增部门，参数：{}", deptDTO);
        deptService.addDept(deptDTO);
        return Result.success();
    }


    @PutMapping
    @Operation(summary = "修改部门")
    @PreAuthorize("hasAuthority('ems:dept:edit')")
    @CacheEvict(cacheNames = "deptTree", allEntries = true) // 删除部门树形结构缓存
    public Result updateDept(@RequestBody DeptAddDTO deptDTO) {
        log.info("修改部门，参数：{}", deptDTO);
        deptService.updateDept(deptDTO);
        return Result.success();

    }


    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('ems:dept:delete')")
    @CacheEvict(cacheNames = "deptTree", allEntries = true) // 删除部门树形结构缓存
    public Result deleteDept(@PathVariable("id") Long id) {
        log.info("删除部门，部门ID：{}", id);
        deptService.deleteDept(id);
        return Result.success();
    }


}
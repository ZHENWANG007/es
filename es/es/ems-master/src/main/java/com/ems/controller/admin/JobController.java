package com.ems.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.ems.common.result.PageResult;
import com.ems.common.result.Result;
import com.ems.domain.dto.job.JobAddDTO;
import com.ems.domain.dto.page.EmpPageDTO;
import com.ems.domain.dto.page.JobPageDTO;
import com.ems.domain.pojo.Job;
import com.ems.domain.vo.Job.JobNameVO;
import com.ems.service.JobService;
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
@RequestMapping("admin/job")
@Tag(name = "岗位管理接口")
@Slf4j
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/page")
    @Operation(summary = "岗位分页查询")
    @PreAuthorize("hasAnyAuthority('ems:job:list')")
    public Result<PageResult> getRoles(JobPageDTO jobPageDTO) {
        log.info("员工分页查询，参数{}", jobPageDTO);
        PageResult pageResult = jobService.pageQuery(jobPageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/nameList")
    @PreAuthorize("hasAnyAuthority('ems:job:list')")
    @Cacheable(cacheNames = "jobNameList")
    public Result<List<JobNameVO>> getJobNameList() {
        log.info("获取岗位名称列表");
        return Result.success(jobService.getJobNameList());
    }

    @PostMapping("/hasEmp")
    @Operation(summary = "获取当前职称是否有员工")
    @PreAuthorize("hasAuthority('ems:job:list')")
    public Result<Boolean> hasEmp(@RequestBody List<Long> ids) {
        log.info("获取当前选择中的部门是否有成员，部门ids：{}", ids);
        Boolean hasEmp = jobService.hasEmp(ids);
        return Result.success(hasEmp);
    }

    @PostMapping
    @Operation(summary = "添加岗位")
    @PreAuthorize("hasAnyAuthority('ems:job:add')")
    @CacheEvict(cacheNames = "jobNameList", allEntries = true)
    public Result addJob(@RequestBody JobAddDTO jobAddDTO) {
        log.info("添加岗位，参数{}", jobAddDTO);
        jobService.saveJob(BeanUtil.copyProperties(jobAddDTO, Job.class));
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改岗位")
    @PreAuthorize("hasAnyAuthority('ems:job:edit')")
    @CacheEvict(cacheNames = "jobNameList", allEntries = true)
    public Result updateJob(@RequestBody JobAddDTO jobAddDTO) {
        log.info("修改岗位，参数{}", jobAddDTO);
        jobService.updateJob(BeanUtil.copyProperties(jobAddDTO, Job.class));
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "批量删除岗位")
    @PreAuthorize("hasAnyAuthority('ems:job:delete')")
    @CacheEvict(cacheNames = "jobNameList", allEntries = true)
    public Result deleteJob(@RequestBody List<Long> ids) {
        log.info("删除岗位，参数{}", ids);
        jobService.deleteJobs(ids);
        return Result.success();
    }

}
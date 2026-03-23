package com.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.page.JobPageDTO;
import com.ems.domain.pojo.Job;
import com.ems.domain.vo.Job.JobNameVO;
import com.ems.domain.vo.Job.JobVO;
import com.ems.mapper.JobMapper;
import com.ems.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.domain.pojo.Emp;
import com.ems.mapper.EmpMapper;
import com.ems.common.context.BaseContext;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final JobMapper jobMapper;
    private final EmpMapper empMapper;

    /**
     * 分页查询岗位信息
     * @param jobPageDTO 分页查询参数
     * @return 分页查询结果
     */
    public PageResult pageQuery(JobPageDTO jobPageDTO) {
        // 分页条件
        Page<Job> page = Page.of(jobPageDTO.getPageNum(), jobPageDTO.getPageSize());

        Page<Job> p = lambdaQuery()
                .like(StringUtils.isNotBlank(jobPageDTO.getName()), Job::getName, jobPageDTO.getName())
                .eq(jobPageDTO.getStatus() != null, Job::getStatus, jobPageDTO.getStatus())
                .page(page);

        List<JobVO> records = p.getRecords().stream()
                .map(job -> BeanUtil.copyProperties(job, JobVO.class))
                .toList();


        return PageResult.builder()
                .total(p.getTotal())
                .records(records)
                .build();
    }

    /**
     * 获取岗位名称列表
     * @return 岗位名称列表
     */
    public List<JobNameVO> getJobNameList() {
        return jobMapper.selectJobName();

    }

    /**
     * 判断岗位是否被使用
     * @param ids 岗位id列表
     * @return 岗位是否被使用的列表
     */
    public Boolean hasEmp(List<Long> ids) {
        // 查询这些岗位下是否有员工
        Long count = empMapper.selectCount(new LambdaQueryWrapper<Emp>()
                .in(Emp::getJobTitle, 
                    // 先查询这些岗位的名称
                    lambdaQuery()
                            .select(Job::getName)
                            .in(Job::getId, ids)
                            .list()
                            .stream()
                            .map(Job::getName)
                            .collect(Collectors.toList())
                )
                .eq(Emp::getStatus, 0)); // 只检查在职员工

        return count > 0;
    }

    /**
     * 新增岗位
     * @param job 岗位信息
     */
    public void saveJob(Job job) {
        // 检查岗位名称是否重复
        Long count = lambdaQuery()
                .eq(Job::getName, job.getName())
                .count();
        if (count > 0) {
            throw new RuntimeException("岗位名称已存在");
        }
        save(job);
    }

    /**
     * 修改岗位
     * @param job 岗位信息
     */
    public void updateJob(Job job) {
        // 检查岗位名称是否重复（排除自身）
        Long count = lambdaQuery()
                .eq(Job::getName, job.getName())
                .ne(Job::getId, job.getId())
                .count();
        if (count > 0) {
            throw new RuntimeException("岗位名称已存在");
        }
        updateById(job);
    }

    /**
     * 删除岗位
     * @param ids 岗位ID列表
     */
    @Transactional
    public void deleteJobs(List<Long> ids) {
        removeBatchByIds(ids);
    }
}
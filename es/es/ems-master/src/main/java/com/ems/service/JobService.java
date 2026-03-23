package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.page.JobPageDTO;
import com.ems.domain.pojo.Job;
import com.ems.domain.vo.Job.JobNameVO;

import java.util.List;


public interface JobService extends IService<Job> {

    /**
     * 分页查询岗位信息
     * @param jobPageDTO 分页查询参数
     * @return 分页查询结果
     */
    PageResult pageQuery(JobPageDTO jobPageDTO);

    /**
     * 获取岗位名称列表
     * @return 岗位名称列表
     */
    List<JobNameVO> getJobNameList();

    /**
     * 判断岗位是否被使用
     * @param ids 岗位id列表
     * @return 岗位是否被使用的列表
     */
    Boolean hasEmp(List<Long> ids);

    /**
     * 新增岗位
     * @param job 岗位信息
     */
    void saveJob(Job job);

    /**
     * 修改岗位
     * @param job 岗位信息
     */
    void updateJob(Job job);

    /**
     * 删除岗位
     * @param ids 岗位ID列表
     */
    void deleteJobs(List<Long> ids);
}

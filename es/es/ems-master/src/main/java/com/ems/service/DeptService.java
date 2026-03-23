package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.domain.dto.dept.DeptAddDTO;
import com.ems.domain.pojo.Dept;
import com.ems.domain.vo.dept.DeptTreeVO;
import com.ems.domain.vo.dept.DeptVO;

import java.util.List;

public interface DeptService extends IService<Dept> {
    /**
     * 获取部门树形结构
     * @return 部门树形结构列表
     */
    List<DeptTreeVO> getDeptTree(String flag);

    /**
     * 删除部门及其子部门和员工
     * @param id 部门ID
     */
    void deleteDept(Long id);

    /**
     * 新增部门
     * @param deptDTO 部门DTO
     */
    void addDept(DeptAddDTO deptDTO);

    /**
     * 修改部门
     * @param deptDTO 部门DTO
     */
    void updateDept(DeptAddDTO deptDTO);

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情
     */
    DeptVO getDeptDetail(Long id);

    /**
     * 判断部门是否有员工 或者 是否存在子部门
     * @param id 部门ID
     * @return 是否有员工
     */
    boolean hasEmp(Long id);

}

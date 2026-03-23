package com.ems.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.common.result.PageResult;
import com.ems.domain.dto.emp.*;
import com.ems.domain.dto.page.EmpPageDTO;
import com.ems.domain.pojo.Emp;
import com.ems.domain.vo.emp.EmpInfoVO;
import com.ems.domain.vo.emp.EmpLoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EmpService extends IService<Emp> {
    /**
     * 管理员登录
     * @param empLoginDTO 管理员登录表单
     * @return 员工登录VO
     */
    EmpLoginVO empLogin(EmpLoginDTO empLoginDTO);

    Map<String, Object> getEmpInfo(Long empId);

    /**
     * 新增员工并分配员工权限
     * @param empAddDTO 员工添加表单
     */
    void addEmp(EmpAddDTO empAddDTO);

    /**
     * 分页查询员工信息
     * @param empPageDTO 员工分页查询参数
     * @return 员工分页结果
     */
    PageResult pageQuery(EmpPageDTO empPageDTO);

    /**
     * Excel导入员工
     * @param file Excel文件
     */
    void importEmp(MultipartFile file);

    /**
     * 删除员工和权限
     * @param ids 员工ID
     */
    void deleteEmp(List<Long> ids);

    /**
     * 重置密码
     * @param ids 员工ID
     */
    void resetPassword(List<Long> ids);

    /**
     * 修改员工对应的角色
     * @param empRoleDTO 员工角色修改表单
     */
    void updateRole(EmpRoleDTO empRoleDTO);

    /**
     * 获取当前登录用户的角色key
     * @return 角色key
     */
    String getRoleKey();

    /**
     * 获取当前登录用户的详细信息
     */
    EmpInfoVO getDetail();

    /**
     * 获取当前登录用户的头像
     * @return 头像地址
     */
    String getAvatar();

    /**
     * 修改密码
     * @param updatePasswordDTO 修改密码表单
     */
    void updatePassword(UpdatePasswordDTO updatePasswordDTO);

    /**
     * 修改头像
     * @param url 头像地址
     */
    void updateAvatar(String url);

    /**
     * 获取导出员工列表
     * @param empPageDTO 员工分页查询参数
     * @return 员工列表
     */
   // List<ImportEmp> getImportEmpList(EmpPageDTO empPageDTO);
}

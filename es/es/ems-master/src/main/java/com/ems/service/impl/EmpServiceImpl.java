package com.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.idev.excel.FastExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.constant.JwtClaimsConstant;
import com.ems.common.context.BaseContext;
import com.ems.common.listener.BaseExcelListener;
import com.ems.common.properties.JwtProperties;
import com.ems.common.result.PageResult;
import com.ems.common.utils.JwtUtil;
import com.ems.common.utils.RedisUtil;
import com.ems.domain.dto.emp.*;
import com.ems.domain.dto.page.EmpPageDTO;
import com.ems.domain.pojo.Dept;
import com.ems.domain.pojo.Emp;
import com.ems.domain.pojo.Role;
import com.ems.domain.vo.emp.EmpInfoVO;
import com.ems.domain.vo.emp.EmpLogin;
import com.ems.domain.vo.emp.EmpLoginVO;
import com.ems.domain.vo.emp.EmpVO;
import com.ems.domain.vo.menu.RouterVO;
import com.ems.mapper.DeptMapper;
import com.ems.mapper.EmpMapper;
import com.ems.mapper.RoleMapper;
import com.ems.service.EmpService;
import com.ems.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {

    private final EmpMapper empMapper;
    private final MenuService menuService;
    private final RoleMapper roleMapper;
    private final DeptMapper deptMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    /**
     * 管理员登录
     */
    public EmpLoginVO empLogin(EmpLoginDTO empLoginDTO) {
        String username = empLoginDTO.getUsername();
        String password = empLoginDTO.getPassword();

        // 1. 封装用户登录表单，创建未认证Authentication对象
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        // 2. 进行校验
        Authentication authenticate = authenticationManager.authenticate(authentication);
        // 3. 获取用户信息
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        EmpLogin empLogin = (EmpLogin) authenticate.getPrincipal();
        Emp emp = empLogin.getEmp();
        if (emp.getStatus() == 1){
            throw new RuntimeException("账号被禁用");
        }
        log.info("员工 {} 登录成功", empLogin.getEmp().getName());

        // 登录成功，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        // 使用fastjson的方法，把对象转换成json字符串
        String loginEmpString = JSON.toJSONString(empLogin);
        claims.put(JwtClaimsConstant.EMP_LOGIN, loginEmpString);
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        // 存储redis白名单
        String tokenKey = "token_" + token;
        redisUtil.set(tokenKey, token, jwtProperties.getTtl()/1000);

        BaseContext.setCurrentId(emp.getId());


        //3、返回实体对象
        return EmpLoginVO.builder()
                .id(emp.getId())
                .token(token)
                .username(emp.getUsername())
                .name(emp.getName())
                .build();

    }

    /**
     * 获取员工信息和权限数据
     * @param empId 员工ID
     * @return 包含员工信息、菜单和权限的Map
     */

    public Map<String, Object> getEmpInfo(Long empId) {

        // 获取员工基本信息
        Emp emp = this.getById(empId);

        // 获取员工对应的角色ID
        Long roleId = roleMapper.getRoleIdByEmpId(empId);

        // 获取路由菜单
        List<RouterVO> routers = menuService.getRoutersByRoleId(roleId);

        // 获取按钮权限
        List<String> buttons = menuService.getPermissionsByRoleId(roleId);

        // 组装员工信息
        Map<String, Object> empInfo = new HashMap<>();
        empInfo.put("id", emp.getId());
        empInfo.put("username", emp.getUsername());
        empInfo.put("name", emp.getName());
        empInfo.put("avatar", emp.getAvatar());
        empInfo.put("roles", empId == 1L ? List.of("admin") : List.of("employee"));

        // 组装返回数据
        Map<String, Object> map = new HashMap<>();
        map.put("empInfo", empInfo);
        map.put("menus", routers);
        map.put("buttons", buttons);
        return map;

    }

    /**
     * 新增员工并分配员工权限
     * @param empAddDTO 员工添加表单
     */
    @Transactional
    public void addEmp(EmpAddDTO empAddDTO) {
        Emp emp = BeanUtil.copyProperties(empAddDTO, Emp.class);
        emp.setPassword(passwordEncoder.encode("123456"));
        this.save(emp);
        // 分配权限员工权限
        empMapper.addEmpRole(List.of(emp.getId()));
    }

    /**
     * 分页查询员工信息
     * @param empPageDTO 员工分页查询参数
     * @return 员工分页结果
     */
    public PageResult pageQuery(EmpPageDTO empPageDTO) {
        // 分页条件
        Page<Emp> page = Page.of(empPageDTO.getPageNum(), empPageDTO.getPageSize());

        Page<Emp> p = lambdaQuery()
                .like(StringUtils.isNotBlank(empPageDTO.getName()), Emp::getName, empPageDTO.getName())
                .like(StringUtils.isNotBlank(empPageDTO.getJobTitle()), Emp::getJobTitle, empPageDTO.getJobTitle())
                .eq(empPageDTO.getDeptId() != null, Emp::getDeptId, empPageDTO.getDeptId())
                .eq(empPageDTO.getStatus() != null, Emp::getStatus, empPageDTO.getStatus())
                .between(empPageDTO.getBeginDate() != null && empPageDTO.getEndDate() != null,
                        Emp::getEntryDate, empPageDTO.getBeginDate(), empPageDTO.getEndDate())
                .page(page);

        List<EmpVO> records = p.getRecords().stream()
                .map(emp -> {
                    EmpVO empVO = BeanUtil.copyProperties(emp, EmpVO.class);
                    // 查询部门名称
                    Dept dept = deptMapper.selectById(emp.getDeptId());

                    if (dept != null) {
                        empVO.setDeptName(dept.getName());
                    }
                    // 查询角色权限标识
                    String roleKey = roleMapper.getRoleKeyByEmpId(emp.getId());
                    if (roleKey != null){
                        empVO.setRoleKey(roleKey);
                    }
                    return empVO;
                })
                .toList();


        return PageResult.builder()
                .total(p.getTotal())
                .records(records)
                .build();
    }

    /**
     * Excel导入员工
     * @param file Excel文件
     */
    @Transactional
    public void importEmp(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空，请选择一个文件上传！");
        }
        try {
            BaseExcelListener<ImportEmp> baseExcelListener = new BaseExcelListener<>();
            FastExcel.read(file.getInputStream(), ImportEmp.class,
                    baseExcelListener).sheet().doRead();
            // 得到读取数据
            List<ImportEmp> dataList = baseExcelListener.getDataList();
            log.info("读取数据：{}", dataList);
            if (dataList.isEmpty()) {
                throw new RuntimeException("Excel数据为空，请选择一个文件上传！");
            }

            // 校验用户名是否重复
            List<String> usernames = dataList.stream()
                    .map(ImportEmp::getUsername)
                    .collect(Collectors.toList());
            
            // 查询已存在的用户名
            List<String> existUsernames = this.lambdaQuery()
                    .select(Emp::getUsername)
                    .in(Emp::getUsername, usernames)
                    .list()
                    .stream()
                    .map(Emp::getUsername)
                    .collect(Collectors.toList());
            
            if (!existUsernames.isEmpty()) {
                throw new RuntimeException("用户名：" + String.join(",", existUsernames) + " 已存在！");
            }

            // 处理数据并转换为Emp对象
            List<Emp> empList = new ArrayList<>();
            for (ImportEmp importEmp : dataList) {
                // 根据部门名称查询部门ID
                Long deptId = deptMapper.getIdByName(importEmp.getDeptName());
                if (deptId == null) {
                    throw new RuntimeException("部门：" + importEmp.getDeptName() + " 不存在！");
                }

                Emp emp = new Emp();
                emp.setDeptId(deptId);
                emp.setUsername(importEmp.getUsername());
                // 设置默认密码123456并加密
                emp.setPassword(passwordEncoder.encode("123456"));
                emp.setName(importEmp.getName());
                emp.setGender("男".equals(importEmp.getGender()) ? 1 : 2);
                emp.setAge(importEmp.getAge());
                emp.setJobTitle(importEmp.getJobTitle());
                emp.setEntryDate(importEmp.getEntryDate());
                emp.setPhone(importEmp.getPhone());
                emp.setEmail(importEmp.getEmail());
                emp.setStatus(0);
                emp.setCreateTime(LocalDateTime.now());
                emp.setCreateUser(BaseContext.getCurrentId());
                empList.add(emp);
            }

            // 批量保存员工数据
            this.saveBatch(empList);

            // 为所有新增员工分配默认角色
            List<Long> empIds = empList.stream()
                    .map(Emp::getId)
                    .collect(Collectors.toList());
            empMapper.addEmpRole(empIds);

        } catch (IOException e) {
            throw new RuntimeException("文件读取失败！");
        } catch (Exception e) {
            throw new RuntimeException("数据导入失败：" + e.getMessage());
        }
    }

    /**
     * 删除员工和权限
     * @param ids 员工ID
     */
    @Transactional
    public void deleteEmp(List<Long> ids) {
        // 删除该员工对应的权限
        empMapper.deleteEmpRole(ids);
        // 删除员工
        this.removeBatchByIds(ids);
    }

    /**
     * 重置密码
     * @param ids 员工ID
     */
    public void resetPassword(List<Long> ids) {
        lambdaUpdate()
                .set(Emp::getPassword, passwordEncoder.encode("123456"))
                .in(Emp::getId, ids)
                .update();
    }

    /**
     * 修改员工对应的角色
     * @param empRoleDTO 员工角色修改表单
     */
    public void updateRole(EmpRoleDTO empRoleDTO) {
        // 修改员工对应的角色
        empMapper.updateEmpRole(empRoleDTO.getEmpId(),empRoleDTO.getRoleId());
    }

    /**
     * 获取当前登录用户的角色key
     * @return 角色key
     */
    public String getRoleKey() {
        Long empId = BaseContext.getCurrentId();
        return roleMapper.getRoleKeyByEmpId(empId);
    }

    /**
     * 获取当前登录用户的详细信息
     */
    public EmpInfoVO getDetail() {
        Long empId = BaseContext.getCurrentId();
        Emp emp = this.getById(empId);
        return EmpInfoVO.builder()
                .id(emp.getId())
                .deptName(deptMapper.getNameByEmpId(empId))
                .username(emp.getUsername())
                .name(emp.getName())
                .gender(emp.getGender())
                .age(emp.getAge())
                .entryDate(emp.getEntryDate())
                .avatar(emp.getAvatar())
                .jobTitle(emp.getJobTitle())
                .phone(emp.getPhone())
                .email(emp.getEmail())
                .build();
    }

    /**
     * 获取当前登录用户的头像
     * @return 头像地址
     */
    public String getAvatar() {
        Long empId = BaseContext.getCurrentId();
        return getById(empId).getAvatar();
    }

    /**
     * 修改密码
     * @param updatePasswordDTO 修改密码表单
     */
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        Long empId = BaseContext.getCurrentId();
        Emp emp = this.getById(empId);
        if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), emp.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        this.lambdaUpdate()
                .set(Emp::getPassword, passwordEncoder.encode(updatePasswordDTO.getNewPassword()))
                .eq(Emp::getId, empId)
                .update();
    }

    /**
     * 修改头像
     * @param url 头像地址
     */
    public void updateAvatar(String url) {
        this.lambdaUpdate()
                .set(Emp::getAvatar, url)
                .eq(Emp::getId, BaseContext.getCurrentId())
                .update();
    }

    /**
     * 获取导出员工列表
     * @param empPageDTO 员工分页查询参数
     * @return 员工列表
     */
//    public List<ImportEmp> getImportEmpList(EmpPageDTO empPageDTO) {
//        // 分页条件
//        Page<Emp> page = Page.of(empPageDTO.getPageNum(), empPageDTO.getPageSize());
//
//        Page<Emp> p = lambdaQuery()
//                .like(StringUtils.isNotBlank(empPageDTO.getName()), Emp::getName, empPageDTO.getName())
//                .like(StringUtils.isNotBlank(empPageDTO.getJobTitle()), Emp::getJobTitle, empPageDTO.getJobTitle())
//                .eq(empPageDTO.getDeptId() != null, Emp::getDeptId, empPageDTO.getDeptId())
//                .eq(empPageDTO.getStatus() != null, Emp::getStatus, empPageDTO.getStatus())
//                .between(empPageDTO.getBeginDate() != null && empPageDTO.getEndDate() != null,
//                        Emp::getEntryDate, empPageDTO.getBeginDate(), empPageDTO.getEndDate())
//                .page(page);
//
//        List<ImportEmp> records = p.getRecords().stream()
//                .map(emp -> {
//                    ImportEmp importEmp = BeanUtil.copyProperties(emp, ImportEmp.class);
//                    // 查询部门名称
//                    Dept dept = deptMapper.selectById(emp.getDeptId());
//
//                    if (dept != null) {
//                        empVO.setDeptName(dept.getName());
//                    }
//                    return empVO;
//                })
//                .toList();
//    }
}
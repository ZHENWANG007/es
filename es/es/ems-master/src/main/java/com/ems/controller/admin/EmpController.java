package com.ems.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import cn.idev.excel.FastExcel;
import com.ems.common.annotation.RepeatSubmit;
import com.ems.common.context.BaseContext;
import com.ems.common.result.PageResult;
import com.ems.common.result.Result;
import com.ems.common.utils.RedisUtil;
import com.ems.domain.dto.emp.*;
import com.ems.domain.dto.page.EmpPageDTO;
import com.ems.domain.pojo.Emp;
import com.ems.domain.vo.emp.EmpInfoVO;
import com.ems.domain.vo.emp.EmpLoginVO;
import com.ems.service.EmpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.cache.annotation.CachePut;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("admin/emp")
@Tag(name = "员工管理接口")
@Slf4j
@RequiredArgsConstructor
public class EmpController {

    private final EmpService empService;
    private final RedisUtil redisUtil;

    /**
     * 员工登录
     * @param empLoginDTO 员工登录信息
     * @return  统一返回结果
     */
    @PostMapping("/login")
    @RepeatSubmit(interval = 5000)
    @Operation(summary = "员工登录")
    public Result<EmpLoginVO> login(@Validated @RequestBody EmpLoginDTO empLoginDTO) {
        log.info("员工：{}，登录成功", empLoginDTO.getUsername());
        EmpLoginVO empLoginVO = empService.empLogin(empLoginDTO);

        return Result.success(empLoginVO);
    }

    /**
     * 员工退出登录
     * @return  统一返回结果
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出登录")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("员工ID：{}，退出登录", BaseContext.getCurrentId());

        String token = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(token)) { // header没有token
            token = request.getParameter("Authorization");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 清除上下文
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            // 清理redis
            redisUtil.del("token_" + token);
            // 清理ThreadLocal
            BaseContext.removeCurrentId();

        }
        return Result.success();
    }

    /**
     * 获取当前登录员工的信息和权限
     * @return  统一返回结果
     */
    @GetMapping("/info")
    @Operation(summary = "获取员工信息和权限")
    public Result<Map<String, Object>> getInfo() {
        // 从ThreadLocal中获取当前登录用户ID
        Long empId = BaseContext.getCurrentId();
        // 获取用户信息和权限数据
        Map<String, Object> map = empService.getEmpInfo(empId);
        return Result.success(map);
    }

    @GetMapping("/avatar")
    @Operation(summary = "获取员工头像")
    public Result<String> getAvatar() {
        return Result.success(empService.getAvatar());
    }

    @GetMapping("/empDetail")
    @Operation(summary = "获取员工基本信息")
    public Result<EmpInfoVO> getDetail() {
        return Result.success(empService.getDetail());
    }

    @GetMapping("/page")
    @Operation(summary = "员工分页查询")
    @PreAuthorize("hasAnyAuthority('ems:employee:list')")
   // @Cacheable(cacheNames = "Emp", key = "#empPageDTO.toString()") // 缓存员工分页列表数据
    public Result<PageResult> getRoles(EmpPageDTO empPageDTO) {
        log.info("员工分页查询，参数{}", empPageDTO);
        PageResult pageResult = empService.pageQuery(empPageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/roleKey")
    @Operation(summary = "获取用户的权限标识")
    public Result<String> getRoleKey() {
        log.info("获取员工用户的权限标识");
        return Result.success(empService.getRoleKey());
    }

    @PostMapping
    @Operation(summary = "新增员工")
    @PreAuthorize("hasAnyAuthority('ems:employee:add')")
   // @CacheEvict(value = "Emp", allEntries = true)
    public Result addEmp(@RequestBody EmpAddDTO empAddDTO) {
        log.info("新增员工，员工信息：{}", empAddDTO);
        empService.addEmp(empAddDTO);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "批量删除员工")
    @PreAuthorize("hasAnyAuthority('ems:employee:delete')")
   // @CacheEvict(value = "Emp", allEntries = true)
    public Result deleteEmp(@RequestBody List<Long> ids) {
        log.info("批量删除员工，员工ID：{}", ids);
        empService.deleteEmp(ids);
        return Result.success();
    }

    @PutMapping("/reset")
    @Operation(summary = "批量重置员工密码")
    @PreAuthorize("hasAnyAuthority('ems:employee:edit')")
   // @CacheEvict(value = "Emp", allEntries = true)
    public Result resetPassword(@RequestBody List<Long> ids) {
        log.info("批量重置员工密码，员工ID：{}", ids);
        empService.resetPassword(ids);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改员工")
    @PreAuthorize("hasAnyAuthority('ems:employee:edit')")
  //  @CacheEvict(value = "Emp", allEntries = true)
    public Result updateEmp(@RequestBody EmpAddDTO empAddDTO) {
        log.info("修改员工，员工信息：{}", empAddDTO);
        Emp emp = BeanUtil.copyProperties(empAddDTO, Emp.class);
        empService.updateById(emp);
        return Result.success();
    }

    @PutMapping("/avatar")
    @Operation(summary = "修改员工对应的头像")
    public Result updateRole(String url) {
        log.info("修改员工对应的头像，{}", url);
        empService.updateAvatar(url);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "用户修改密码")
    public Result updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        log.info("用户修改密码，{}", updatePasswordDTO);
        empService.updatePassword(updatePasswordDTO);
        return Result.success();
    }

    @PutMapping("/detail")
    @Operation(summary = "用户修改基本信息")
    public Result updateDetail(@RequestBody EmpDetailDTO empDetailDTO) {
        log.info("用户修改基本信息，{}", empDetailDTO);
        Emp emp = BeanUtil.copyProperties(empDetailDTO, Emp.class);
        emp.setId(BaseContext.getCurrentId());
        empService.updateById(emp);
        return Result.success();
    }


    @PutMapping("/role")
    @Operation(summary = "修改员工对应的角色")
    @PreAuthorize("hasAnyAuthority('ems:employee:role')")
  //  @CacheEvict(value = "Emp", allEntries = true)
    public Result updateRole(@RequestBody EmpRoleDTO empRoleDTO) {
        log.info("修改员工对应的角色，{}", empRoleDTO);
        empService.updateRole(empRoleDTO);
        return Result.success();
    }


    @PostMapping("/import")
    @Operation(summary = "Excel导入")
    @PreAuthorize("hasAuthority('ems:employee:import')")
  //  @CacheEvict(value = "Emp", allEntries = true)
    public Result importEmp(@RequestParam("file") MultipartFile file){
        log.info("Excel导入，文件：{}", file.getOriginalFilename());
        empService.importEmp(file);
        return Result.success();
    }

    @GetMapping("/template")
    @Operation(summary = "下载Excel员工表模版")
    @PreAuthorize("hasAuthority('ems:employee:export')")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        log.info("下载Excel员工表模版");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("员工表", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition",
                "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 写入数据
        FastExcel.write(response.getOutputStream(), ImportEmp.class)
                .sheet("模板")
                .doWrite(buildData());
    }


    @GetMapping("/export")
    @Operation(summary = "下载Excel员工表")
    @PreAuthorize("hasAuthority('ems:employee:export')")
    public void downloadExcel(HttpServletResponse response, EmpPageDTO empPageDTO) throws IOException {
        log.info("下载Excel员工表");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("员工表", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition",
                "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 准备数据
   //     List<ImportEmp> empList = empService.getImportEmpList(empPageDTO);
        // 写入数据
        FastExcel.write(response.getOutputStream(), ImportEmp.class)
                .sheet("模板")
                .doWrite(buildData());
    }

    private List<ImportEmp> buildData() {
        ImportEmp emp1 = ImportEmp.builder()
                .id(1L)
                .deptName("研发部")
                .username("admin")
                .name("张三")
                .gender("男")
                .age(18)
                .jobTitle("Java开发")
                .entryDate(LocalDate.of(2023, 1, 1))
                .phone("13800000000")
                .email("admin@example.com")
                .roleName("管理员")
                .build();
        return new ArrayList<>(List.of(emp1));
    }





}
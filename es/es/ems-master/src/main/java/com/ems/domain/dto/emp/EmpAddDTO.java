package com.ems.domain.dto.emp;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@Tag(name = "EmpAddDTO", description = "员工添加/修改DTO")
public class EmpAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2379442950446509405L;

    @Schema(description = "员工ID")
    private Long id;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "职位名称")
    private String jobTitle;

    @Schema(description = "入职时间")
    private LocalDate entryDate;

    @Schema(description = "离职时间")
    private LocalDate leaveDate;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "状态: 0-正常, 1-禁用")
    private Integer status;
}
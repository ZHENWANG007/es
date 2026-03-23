package com.ems.domain.vo.emp;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 员工VO
 */
@Data
@Builder
@Tag(name = "EmpVO", description = "员工VO")
public class EmpVO {

    @Schema(description = "员工ID")
    private Long id;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "职位名称")
    private String jobTitle;

    @Schema(description = "入职时间")
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate entryDate;

    @Schema(description = "离职时间")
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate leaveDate;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "权限标识")
    private String roleKey;

    @Schema(description = "状态: 0-正常, 1-禁用")
    private Integer status;
}
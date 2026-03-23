package com.ems.domain.vo.emp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


/**
 * 登录员工的详细信息
 */
@Data
@Builder
@Tag(name = "EmpInfoVO", description = "登录员工的详细信息")
public class EmpInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7461087867047901467L;

    @Schema(description = "员工ID")
    private Long id;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别，1-男 2-女")
    private Integer gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "入职日期")
    private LocalDate entryDate;


    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "职位名称")
    private String jobTitle;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;


}
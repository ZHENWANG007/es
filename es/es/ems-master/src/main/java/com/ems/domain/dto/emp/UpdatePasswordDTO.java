package com.ems.domain.dto.emp;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;


@Data
@Tag(name = "UpdatePasswordDTO", description = "员工修改密码DTO")
public class UpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
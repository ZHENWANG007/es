package com.ems.domain.dto.role;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Tag(name = "RoleAddDTO", description = "角色添加/修改DTO")
public class RoleAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4740035890200035669L;

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "权限标识")
    private String roleKey;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "角色状态: 0-正常, 1-禁用")
    private String status;
}
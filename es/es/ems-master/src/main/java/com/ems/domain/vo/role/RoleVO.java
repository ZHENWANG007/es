package com.ems.domain.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Tag(name = "RoleVO", description = "角色VO")
public class RoleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8523330729151854867L;

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
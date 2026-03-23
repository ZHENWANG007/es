package com.ems.domain.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description TODO
 * @create 2024-12-28  16:02
 */
@Data
@Tag(name = "RoleMenusAddDTO", description = "角色分配菜单权限DTO")
public class RoleMenusAddDTO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "菜单ID数组")
    private Integer[] menuIds;
}
package com.ems.domain.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jiang YinHui
 * @create 2024-12-28  14:17
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "RolesPageDTO", description = "角色分页查询参数")
public class RolesPageDTO extends PageDTO{

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限标识")
    private String roleKey;

    @Schema(description = "角色状态")
    private  Integer status;
}
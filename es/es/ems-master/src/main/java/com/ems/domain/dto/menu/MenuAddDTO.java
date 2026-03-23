package com.ems.domain.dto.menu;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Tag(name = "MenuAddDTO", description = "菜单添加/修改DTO")
public class MenuAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -386577308310294220L;

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "显示状态: 0-显示, 1-隐藏")
    private String visible;

    @Schema(description = "菜单状态: 0-正常, 1-禁用")
    private String status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "菜单类型: M-菜单, B-按钮")
    private Character type;
}
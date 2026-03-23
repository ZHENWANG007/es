package com.ems.domain.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description 菜单类
 * @create 2024-10-23  15:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "Menu", description = "菜单实体")
public class Menu implements Serializable {

    @Serial
    private static final long serialVersionUID = -4841134990905297509L;

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
    private Integer visible;

    @Schema(description = "菜单状态: 0-正常, 1-禁用")
    private Integer status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "菜单类型: M-菜单, B-按钮")
    private Character type;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT) //执行插入操作时自动填充
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)//执行插入和更新操作时自动填充
    private LocalDateTime updateTime;

    @Schema(description = "创建者ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @Schema(description = "更新者ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    @TableField(exist = false)
    @Schema(description = "子菜单列表")
    private List<Menu> children;
}
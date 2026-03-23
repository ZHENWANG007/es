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

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description 角色类
 * @create 2024-10-23  15:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "Role", description = "角色实体")
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 9066016360623530358L;

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

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT) //执行插入操作时自动填充
    private LocalDateTime createTime;

    @Schema(description = "创建者ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)//执行插入和更新操作时自动填充
    private LocalDateTime updateTime;

    @Schema(description = "更新者ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
package com.ems.domain.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description 角色菜单关联类
 * @create 2024-10-23  15:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "RoleMenu", description = "角色菜单关联实体")
public class RoleMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 6414035581680654195L;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "菜单ID")
    private Long menuId;

}
package com.ems.domain.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 岗位实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "Job", description = "岗位实体类")
public class Job implements Serializable {

    @Serial
    private static final long serialVersionUID = -4298570788738737127L;

    @Schema(description = "岗位ID")
    private Long id;

    @Schema(description = "岗位名称")
    private String name;

    @Schema(description = "岗位职责描述")
    private String description;

    @Schema(description = "最低薪资")
    private BigDecimal minSalary;

    @Schema(description = "最高薪资")
    private BigDecimal maxSalary;

    @Schema(description = "状态: 0-正常, 1-禁用")
    private Integer status;

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
}

package com.ems.domain.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Jiang YinHui
 * @create 2024-12-31  15:13
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "JobPageDTO", description = "岗位分页查询参数")
public class JobPageDTO extends PageDTO{

    @Schema(description = "岗位名称")
    private String name;

    @Schema(description = "状态: 0-正常, 1-禁用")
    private Integer status;

}
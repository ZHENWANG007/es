package com.ems.domain.dto.job;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Tag(name = "JobAddDTO", description = "岗位添加/修改DTO")
public class JobAddDTO {

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

}
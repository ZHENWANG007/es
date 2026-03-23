package com.ems.domain.dto.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Tag(name = "DeptAddDTO", description = "部门添加/修改DTO")
public class DeptAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9005756235081585115L;

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父部门ID")
    private Long parentId;

    @Schema(description = "部门描述")
    private String description;

    @Schema(description = "排序")
    private Integer orderNum;

    @Schema(description = "状态: 0-正常, 1-禁用")
    private Integer status;

}
package com.ems.domain.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "DeptTreeVO", description = "部门树")
public class DeptTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7110803135399244807L;

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

    @Schema(description = "子部门列表")
    private List<DeptTreeVO> children;
}
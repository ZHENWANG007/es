package com.ems.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "部门员工数量统计VO")
public class DeptEmpCountVO {
    
    @Schema(description = "部门名称")
    private String name;
    
    @Schema(description = "员工数量")
    private Integer value;
} 
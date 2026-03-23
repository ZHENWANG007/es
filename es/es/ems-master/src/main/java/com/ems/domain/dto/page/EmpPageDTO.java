package com.ems.domain.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Jiang YinHui
 * @create 2024-12-29  20:23
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "EmpPageDTO", description = "员工分页查询参数")
public class EmpPageDTO extends PageDTO{

    @Schema(description = "员工姓名")
    private String name;

    @Schema(description = "员工部门ID")
    private Long deptId;

    @Schema(description = "职位名称")
    private String jobTitle;

    @Schema(description = "员工状态")
    private Integer status;

    @Schema(description = "权限标识")
    private String roleKey;

    @Schema(description = "入职开始时间")
    private LocalDate beginDate;

    @Schema(description = "入职结束时间")
    private LocalDate endDate;
}
package com.ems.domain.vo.Job;

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
 * 岗位VO
 */
@Data
@Builder
@Tag(name = "JobVO", description = "岗位VO")
public class JobVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8140518662428075472L;

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

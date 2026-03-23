package com.ems.domain.vo.Job;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 返回的岗位名称和ID VO
 */
@Data
@Builder
@Tag(name = "JobVO", description = "岗位名称和ID VO")
public class JobNameVO {

    @Schema(description = "岗位ID")
    private Long id;

    @Schema(description = "岗位名称")
    private String name;

}
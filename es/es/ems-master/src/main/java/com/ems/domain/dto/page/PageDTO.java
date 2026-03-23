package com.ems.domain.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jiang YinHui
 * @create 2024-12-28  14:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "PageDTO", description = "分页基础参数")
public class PageDTO {

    @Schema(description = "当前页码")
    private Integer pageNum = 0;

    @Schema(description = "页面大小")
    private Integer pageSize = 10;
}
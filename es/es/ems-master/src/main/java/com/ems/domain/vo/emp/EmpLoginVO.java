package com.ems.domain.vo.emp;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录响应对象
 */
@Data
@Builder
@Tag(name = "EmpLoginVO", description = "员工登录响应")
public class EmpLoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4393557997355879737L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "令牌")
    private String token;
}

package com.ems.domain.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "ClockDTO", description = "打卡DTO")
public class ClockDTO {

    @Schema(description = "打卡类型：IN-上班打卡，OUT-下班打卡")
    private String type;

}
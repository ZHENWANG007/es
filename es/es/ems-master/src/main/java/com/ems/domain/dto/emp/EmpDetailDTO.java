package com.ems.domain.dto.emp;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDate;


@Data
public class EmpDetailDTO {

    private String name;

    private Integer gender;

    private Integer age;

    private String phone;

    private String email;
}
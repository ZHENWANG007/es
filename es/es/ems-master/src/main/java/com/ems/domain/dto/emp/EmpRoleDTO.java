package com.ems.domain.dto.emp;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Tag(name = "EmpRoleDTO", description = "修改员工角色")
public class EmpRoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8561775933830750100L;

    private Long empId;

    private Long roleId;

}
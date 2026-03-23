package com.ems.domain.vo.emp;

import com.ems.domain.vo.menu.RouterVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Tag(name = "EmpPermissionsVO", description = "员工权限列表")
public class EmpPermissionsVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8014447876649544761L;

    private EmpInfoVO empInfo;

    private List<RouterVO> menus;
}
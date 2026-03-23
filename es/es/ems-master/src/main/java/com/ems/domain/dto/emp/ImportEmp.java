package com.ems.domain.dto.emp;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportEmp {

    @ExcelProperty(value = "序号", index = 0)
    private Long id;

    @ExcelProperty(value = "部门名称", index = 1)
    private String deptName;

    @ExcelProperty(value = "用户名", index = 2)
    private String username;

    @ExcelProperty(value = "姓名", index = 3)
    private String name;

    @ExcelProperty(value = "性别", index = 4)
    private String gender;

    @ExcelProperty(value = "年龄", index = 5)
    private Integer age;

    @ExcelProperty(value = "职位名称", index = 6)
    private String jobTitle;

    @ExcelProperty(value = "入职时间", index = 7)
    private LocalDate entryDate;

    @ExcelProperty(value = "联系电话", index = 8)
    private String phone;

    @ExcelProperty(value = "电子邮箱", index = 9)
    private String email;

    @ExcelProperty(value = "角色名称", index = 10)
    private String roleName;

}
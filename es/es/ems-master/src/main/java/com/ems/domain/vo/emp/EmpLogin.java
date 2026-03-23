package com.ems.domain.vo.emp;

import com.alibaba.fastjson2.annotation.JSONField;
import com.ems.domain.pojo.Emp;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description UserDetails的实现类
 * @create 2024-12-23  22:32
 */
@Data
@NoArgsConstructor
public class EmpLogin implements UserDetails {

    @Serial
    private static final long serialVersionUID = 7330836274775504268L;

    public EmpLogin(Emp emp, List<String> list) {
        this.emp = emp;
        this.list = list;
    }

    // 权限列表
    private List<String> list;

    private Emp emp;

    //自定义一个权限列表的集合，中转操作
    @JSONField(serialize = false) //在序列化对象时忽略该字段
    private List<SimpleGrantedAuthority> authorities;

    // 用于返回权限信息
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        authorities = new ArrayList<>();
        for (String item : list) {
            if (item != null && !item.trim().isEmpty()) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(item);
                authorities.add(authority);
            }
        }
        return authorities;
    }

    // 获取密码
    @Override
    public String getPassword() {
        return emp.getPassword();
    }

    // 获取用户名
    @Override
    public String getUsername() {
        return emp.getUsername();
    }

    // 账号是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 判断账号是否没有锁定
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 判断账户是否没有超时
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 判断账号是否可用
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
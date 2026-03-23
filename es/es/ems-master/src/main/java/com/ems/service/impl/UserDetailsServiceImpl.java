package com.ems.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ems.domain.pojo.Emp;
import com.ems.domain.vo.emp.EmpLogin;
import com.ems.mapper.EmpMapper;
import com.ems.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description TODO
 * @create 2024-12-23  22:36
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmpMapper empMapper;
    private final MenuMapper menuMapper;

    /**
     * 根据用户名查询用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username.isEmpty()){
            throw new InternalAuthenticationServiceException("");
        }
        //  根据用户名查询用户信息
        QueryWrapper<Emp> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Emp emp = empMapper.selectOne(wrapper);
        // 判断是否查到用户 如果没查到抛出异常
        if (ObjectUtil.isNull(emp)){
            throw new UsernameNotFoundException("");
        }
        // 2.赋权操作 死数据
        // 赋权操作
        List<String> list = menuMapper.getMenuByUserId(emp.getId());

        for (String s : list) {
            System.out.println(s);
        }

        return new EmpLogin(emp, list);
    }
}
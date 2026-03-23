package com.ems.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiang YinHui
 * @version 1.0
 * @description TODO
 * @create 2024-12-31  14:24
 */
@RestController("/user")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

}
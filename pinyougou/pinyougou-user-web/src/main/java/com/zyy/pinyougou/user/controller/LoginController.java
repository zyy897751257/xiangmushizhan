package com.zyy.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Zyy
 * @date: 2019-07-08 10:24
 * @description:
 * @version:
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getUsername")
    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

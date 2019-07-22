package com.zyy.pinyougou.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/page/login")
    public String showLogin(String url) {
        return "redirect:" + url;
    }
}

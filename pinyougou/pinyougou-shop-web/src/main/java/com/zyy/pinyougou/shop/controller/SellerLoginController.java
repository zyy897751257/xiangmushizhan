package com.zyy.pinyougou.shop.controller;

import com.zyy.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Zyy
 * @date: 2019-06-23 20:32
 * @description:
 * @version:
 */
@RestController
@RequestMapping("/login")
public class SellerLoginController {

    @RequestMapping("/getSellername")
    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

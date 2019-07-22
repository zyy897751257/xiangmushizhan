package com.zyy.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.cart.service.CartService;
import com.zyy.pinyougou.common.CookieUtil;
import com.zyy.pinyougou.entity.Cart;
import com.zyy.pinyougou.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);

        if ("anonymousUser".equals(username)) {
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (StringUtils.isEmpty(cartListString)) {
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);

            return cookieCartList;
        } else {
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
            if (cartListFromRedis == null) {
                cartListFromRedis = new ArrayList<>();
            }
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (StringUtils.isEmpty(cartListString)) {
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
            if (cookieCartList.size() > 0) {
                //合并
                List<Cart> mergeCartList = cartService.mergeCartList(cookieCartList, cartListFromRedis);

                cartListFromRedis = mergeCartList;
                cartService.saveCartListToRedis(username, mergeCartList);

                CookieUtil.deleteCookie(request, response, "cartList");
            }
            return cartListFromRedis;
        }
    }

    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");//统一指定的域访问我的服务器资源
        response.setHeader("Access-Control-Allow-Credentials", "true");//同意客户端携带cookie
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);

        try {
            if ("anonymousUser".equals(username)) {
                List<Cart> cartList = findCartList(request,response);
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                CookieUtil.setCookie(request,response,"cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
                return new Result(true,"添加成功");
            } else {
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
                List<Cart> cartListNew = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                cartService.saveCartListToRedis(username, cartListNew);
                return new Result(true,"保存成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}

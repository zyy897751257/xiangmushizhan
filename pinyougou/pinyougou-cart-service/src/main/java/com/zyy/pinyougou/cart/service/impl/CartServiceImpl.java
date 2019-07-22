package com.zyy.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyy.pinyougou.cart.service.CartService;
import com.zyy.pinyougou.entity.Cart;
import com.zyy.pinyougou.mapper.TbItemMapper;
import com.zyy.pinyougou.pojo.TbItem;
import com.zyy.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.ClusterRedirectException;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Zyy
 * @date: 2019-07-08 19:55
 * @description:
 * @version:
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //2.获取商家ID
        String sellerId = tbItem.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart  cart = findCartBySellerId(sellerId,cartList);

        if (cartList == null) {
            cartList = new ArrayList();
        }

        if (cart == null) {
            //4.如果购物车列表中不存在该商家的购物车
            //4.1 新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());

            List<TbOrderItem> orderItemList = new ArrayList();

            TbOrderItem tbOrderItem = new TbOrderItem();
            tbOrderItem.setItemId(itemId);
            tbOrderItem.setGoodsId(tbItem.getGoodsId());
            tbOrderItem.setTitle(tbItem.getTitle());
            tbOrderItem.setPrice(tbItem.getPrice());
            tbOrderItem.setNum(num);
            double v = num * tbItem.getPrice().doubleValue();
            tbOrderItem.setTotalFee(new BigDecimal(v));
            tbOrderItem.setPicPath(tbItem.getImage());

            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        } else {
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = findOrderItemByItemId(itemId,orderItemList);

            if (orderItem == null) {
                //5.1. 如果没有，新增购物车明细
                TbOrderItem orderItemNew = new TbOrderItem();
                orderItemNew.setItemId(itemId);
                orderItemNew.setGoodsId(tbItem.getGoodsId());
                orderItemNew.setTitle(tbItem.getTitle());
                orderItemNew.setPrice(tbItem.getPrice());
                orderItemNew.setNum(num);
                double v = num * tbItem.getPrice().doubleValue();
                orderItemNew.setTotalFee(new BigDecimal(v));
                orderItemNew.setPicPath(tbItem.getImage());
                orderItemList.add(orderItemNew);

            } else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                double v = orderItem.getNum() * orderItem.getPrice().doubleValue();
                orderItem.setTotalFee(new BigDecimal(v));

                //判断如果商品的购买数量为0 表示不买了，就要删除商品
                if (orderItem.getNum() == 0) {
                    orderItemList.remove(orderItem);
                }
                //如果是长度为空说明 用户没购买该商家的商品就直接删除对象
                if (orderItemList.size() == 0) {//[]
                    cartList.remove(cart);//商家也删除了
                }
            }

        }

        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        return (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieList, List<Cart> redisList) {
        for (Cart cart : cookieList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                redisList = addGoodsToCartList(redisList, orderItem.getItemId(), orderItem.getNum());
            }
        }

        return redisList;
    }

    private TbOrderItem findOrderItemByItemId(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }

        return null;
    }

    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }

        return null;
    }

}

package com.zyy.pinyougou.entity;

import com.zyy.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Zyy
 * @date: 2019-07-08 15:18
 * @description:
 * @version:
 */
public class Cart implements Serializable {

    private String sellerId;//商家ID
    private String sellerName;//商家名称
    private List<TbOrderItem> orderItemList;//购物车明细

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}

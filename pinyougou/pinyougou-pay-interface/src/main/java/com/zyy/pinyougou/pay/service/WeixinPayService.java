package com.zyy.pinyougou.pay.service;

import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-07-11 19:52
 * @description:
 * @version:
 */
public interface WeixinPayService {

    public Map createNative(String out_trade_no,String total_fee);

    public Map queryPayStatus(String out_trade_no);

    Map closePay(String out_trade_no);
}

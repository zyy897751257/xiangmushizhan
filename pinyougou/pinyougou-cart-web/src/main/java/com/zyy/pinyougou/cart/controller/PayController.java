package com.zyy.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyy.pinyougou.common.IdWorker;
import com.zyy.pinyougou.entity.Result;
import com.zyy.pinyougou.order.service.OrderService;
import com.zyy.pinyougou.pay.service.WeixinPayService;
import com.zyy.pinyougou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-07-11 20:16
 * @description:
 * @version:
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);
        if (tbPayLog != null) {
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee()+"");
        }
        return null;
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = new Result();
        try {
            int count = 0;
            while (true) {
                Map map = weixinPayService.queryPayStatus(out_trade_no);
                count++;

                if (count >= 100) {
                    result.setSuccess(false);
                    result.setMessage("支付超时");
                    break;
                }
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    result.setSuccess(true);
                    result.setMessage("支付成功");
                    orderService.updateOrderStatus(out_trade_no, (String) map.get("transaction_id"));
                    break;
                }

                Thread.sleep(3000);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}

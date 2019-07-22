package com.zyy.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyy.pinyougou.entity.Result;
import com.zyy.pinyougou.pay.service.WeixinPayService;
import com.zyy.pinyougou.pojo.TbPayLog;
import com.zyy.pinyougou.pojo.TbSeckillOrder;
import com.zyy.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
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
    private SeckillOrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        /*TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);
        if (tbPayLog != null) {
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee()+"");
        }*/
        TbSeckillOrder seckillOrder = orderService.getUserOrderStatus(userId);
        if (seckillOrder != null) {
            long v = (long) (seckillOrder.getMoney().doubleValue()*100);
            return weixinPayService.createNative(seckillOrder.getId() + "",v + "");
        }
        return null;
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = new Result(false,"支付失败");
        try {
            int count = 0;
            while (true) {
                Map resultMap = weixinPayService.queryPayStatus(out_trade_no);
                count++;

                if (count >= 100) {
                    result.setSuccess(false);
                    result.setMessage("支付超时");
                    //关闭微信订单
                    Map map = weixinPayService.closePay(out_trade_no);
                    if ("ORDERPAID".equals(map.get("err_code"))) {
                        //已支付
                        orderService.updateOrderStatus(userId, (String) resultMap.get("transaction_id"));
                    } else if ("SUCCESS".equals(map.get("result_code")) || "ORDERCLOSED".equals(map.get("err_code"))) {
                        //删除预订单
                        orderService.deleteOrder(userId);
                    } else {
                        System.out.println("由于微信端错误");
                    }
                    break;
                }

                Thread.sleep(3000);

                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result.setSuccess(true);
                    result.setMessage("支付成功");
                    orderService.updateOrderStatus(userId, (String) resultMap.get("transaction_id"));
                    break;
                }

            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}

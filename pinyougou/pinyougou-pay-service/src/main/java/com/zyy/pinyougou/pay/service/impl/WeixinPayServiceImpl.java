package com.zyy.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.zyy.pinyougou.common.HttpClient;
import com.zyy.pinyougou.pay.service.WeixinPayService;
import org.elasticsearch.search.aggregations.pipeline.movavg.models.EwmaModel;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-07-11 19:58
 * @description:
 * @version:
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appId}")
    private String appId;

    @Value("${partner}")
    private String partner;

    @Value("${partnerKey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> param = new HashMap();
        param.put("appid", appId);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("total_fee", total_fee);//总金额(分)
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://test.itcast.cn");//回调地址
        param.put("trade_type", "NATIVE");//交易类型
        try {
            //2.生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3.获取结果
            String result = client.getContent();
            System.out.println("结果:"+result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String,String> map = new HashMap();
            map.put("code_url", resultMap.get("code_url"));
            map.put("total_fee", total_fee);
            map.put("out_trade_no", out_trade_no);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param = new HashMap();
        param.put("appid", appId);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map closePay(String out_trade_no) {

        try {
            Map param = new HashMap();
            param.put("appid", appId);//公众账号ID
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//订单号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            String url="https://api.mch.weixin.qq.com/pay/closeorder";
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

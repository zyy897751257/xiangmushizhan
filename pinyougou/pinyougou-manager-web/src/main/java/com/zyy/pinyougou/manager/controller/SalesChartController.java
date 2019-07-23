package com.zyy.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyy.pinyougou.pojo.TbSeller;
import com.zyy.pinyougou.sellergoods.service.SalesChartService;
import com.zyy.pinyougou.sellergoods.service.SellerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.net.www.protocol.http.HttpURLConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/salesChart")
public class SalesChartController {

    @Reference
    private SellerService sellerService;

    @Reference
    private SalesChartService salesChartService;

    @RequestMapping("/getSalesChart")
    public Map getSalesChart(String startDate, String endDate) {
        Map<String,Object> map = new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            if (!start.after(end)) {
                //判断起始日期是否符合
                map.put("flag", true);
                List<String> sellerList = new ArrayList();
                List<JSONObject> saleCounts = new ArrayList();
                List<TbSeller> tbSellers = sellerService.selectAll();
                JSONObject object1 = new JSONObject();
                int count = 5;
                for (TbSeller tbSeller : tbSellers) {
                    boolean flag = false;
                    sellerList.add(tbSeller.getName());
                    if (count > 0 ) {
                        flag = true;
                    }
                    count--;
                    object1.put(tbSeller.getName(),flag);
                    double saleCount = salesChartService.getSaleCountsByDateAndSellerId(start,end,tbSeller.getSellerId());
                    JSONObject object2 = new JSONObject();
                    object2.put("name",tbSeller.getName());
                    object2.put("value",saleCount);
                    saleCounts.add(object2);
                }
                map.put("allSeller",sellerList);
                map.put("seriesData",saleCounts);
                map.put("selected",object1);
            } else {
                map.put("flag", false);
                map.put("message", "截止日期不能小于开始日期");
            }

            return map;
        } catch (ParseException e) {
            e.printStackTrace();
            map.put("flag", false);
            map.put("message", "服务器异常");
            return map;
        }

    }
}

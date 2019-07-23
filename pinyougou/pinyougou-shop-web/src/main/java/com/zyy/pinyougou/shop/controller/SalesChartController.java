package com.zyy.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyy.pinyougou.entity.Result;
import com.zyy.pinyougou.sellergoods.service.SalesChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Zyy
 * @date: 2019-07-22 19:18
 * @description:
 * @version:
 */
@RestController
@RequestMapping("/salesChart")
public class SalesChartController {

    @Reference
    private SalesChartService salesChartService;

    @RequestMapping("/getSalesChart")
    public Map getSalesChart(String startDate, String endDate) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,Object> map = new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            if (!start.after(end)) {
                //判断起始日期是否符合
                map.put("flag", true);
                List<String> dates = new ArrayList();
                List<Double> saleCounts = new ArrayList();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                while (true) {
                    Date date = calendar.getTime();
                    dates.add(dateFormat.format(date));
                    saleCounts.add(salesChartService.getSaleCountsByDate(date,userId));
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    if (calendar.getTime().after(end)) {
                        break;
                    }
                }
                map.put("dates", dates);
                map.put("saleCounts",saleCounts);
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

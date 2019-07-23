package com.zyy.pinyougou.sellergoods.service;

import java.util.Date;

public interface SalesChartService {

    double getSaleCountsByDate(Date date,String sellerId);

    double getSaleCountsByDateAndSellerId(Date start, Date end, String sellerId);
}

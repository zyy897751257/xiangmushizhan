package com.zyy.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyy.pinyougou.mapper.TbOrderMapper;
import com.zyy.pinyougou.mapper.TbSeckillOrderMapper;
import com.zyy.pinyougou.pojo.TbOrder;
import com.zyy.pinyougou.pojo.TbSeckillOrder;
import com.zyy.pinyougou.sellergoods.service.SalesChartService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SalesChartServiceImpl implements SalesChartService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Override
    public double getSaleCountsByDate(Date date,String sellerId) {
        BigDecimal counts = new BigDecimal("0");
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.DAY_OF_MONTH,1);
        Date date1 = instance.getTime();
        Example example1 = new Example(TbOrder.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andGreaterThanOrEqualTo("createTime",date);
        criteria1.andEqualTo("status","2");
        criteria1.andLessThan("createTime",date1);
        criteria1.andEqualTo("sellerId",sellerId);
        List<TbOrder> tbOrderList = orderMapper.selectByExample(example1);
        if (tbOrderList != null && tbOrderList.size() > 0) {
            for (TbOrder tbOrder : tbOrderList) {
                BigDecimal payment = tbOrder.getPayment();
                counts = counts.add(payment);
            }
        }

        Example example2 = new Example(TbSeckillOrder.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andGreaterThanOrEqualTo("createTime",date);
        criteria2.andEqualTo("status","1");
        criteria2.andLessThan("createTime",date1);
        criteria2.andEqualTo("sellerId",sellerId);
        List<TbSeckillOrder> seckillOrderList = seckillOrderMapper.selectByExample(example2);
        if (seckillOrderList != null && seckillOrderList.size() > 0) {
            for (TbSeckillOrder seckillOrder : seckillOrderList) {
                BigDecimal money = seckillOrder.getMoney();
                counts = counts.add(money);
            }
        }

        return counts.doubleValue();
    }

    @Override
    public double getSaleCountsByDateAndSellerId(Date start, Date end, String sellerId) {
        BigDecimal counts = new BigDecimal("0");
        Example example1 = new Example(TbOrder.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andGreaterThanOrEqualTo("createTime",start);
        criteria1.andEqualTo("status","2");
        criteria1.andLessThan("createTime",end);
        criteria1.andEqualTo("sellerId",sellerId);
        List<TbOrder> tbOrderList = orderMapper.selectByExample(example1);
        if (tbOrderList != null && tbOrderList.size() > 0) {
            for (TbOrder tbOrder : tbOrderList) {
                BigDecimal payment = tbOrder.getPayment();
                counts = counts.add(payment);
            }
        }

        Example example2 = new Example(TbSeckillOrder.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andGreaterThanOrEqualTo("createTime",start);
        criteria2.andEqualTo("status","1");
        criteria2.andLessThan("createTime",end);
        criteria2.andEqualTo("sellerId",sellerId);
        List<TbSeckillOrder> seckillOrderList = seckillOrderMapper.selectByExample(example2);
        if (seckillOrderList != null && seckillOrderList.size() > 0) {
            for (TbSeckillOrder seckillOrder : seckillOrderList) {
                BigDecimal money = seckillOrder.getMoney();
                counts = counts.add(money);
            }
        }

        return counts.doubleValue();
    }
}

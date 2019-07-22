package com.zyy.pinyougou.seckill.thread;

import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.common.IdWorker;
import com.zyy.pinyougou.common.SysConstants;
import com.zyy.pinyougou.common.pojo.MessageInfo;
import com.zyy.pinyougou.core.service.CoreService;
import com.zyy.pinyougou.mapper.TbSeckillGoodsMapper;
import com.zyy.pinyougou.pojo.TbSeckillGoods;
import com.zyy.pinyougou.pojo.TbSeckillOrder;
import com.zyy.pinyougou.seckill.pojo.SeckillStatus;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.sql.SQLOutput;
import java.util.Date;

/**
 * @author: Zyy
 * @date: 2019-07-14 18:16
 * @description:
 * @version:
 */
public class CreateOrderThread {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private DefaultMQProducer mqProducer;

    @Async
    public void handlerOrder() {
        try {
            //模拟订单处理
            System.out.println("模拟订单处理开始=========" + Thread.currentThread().getName());
            Thread.sleep(10000);
            System.out.println("模拟订单处理结束 总共耗时10秒 =========" + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();

        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillStatus.getGoodsId());

        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillStatus.getGoodsId(),seckillGoods);

        if (seckillGoods.getStockCount() <= 0) {
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillStatus.getGoodsId());
            tbSeckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
        }

        long orderId = idWorker.nextId();

        //将订单存在redis中
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(orderId);
        seckillOrder.setSeckillId(seckillStatus.getGoodsId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(seckillStatus.getUserId());
        seckillOrder.setStatus("0");

        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(seckillStatus.getUserId(),seckillOrder);

        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());

        sendDelayMessage(seckillOrder);

    }

    private void sendDelayMessage(TbSeckillOrder seckillOrder) {

        try {
            MessageInfo messageInfo = new MessageInfo(seckillOrder, "TOPIC_SECKILL_DELAY", "Tags_SECKILL_DELAY", "handleOrder", MessageInfo.METHOD_UPDATE);

            Message message = new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(seckillOrder).getBytes());

            message.setDelayTimeLevel(16);

            mqProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

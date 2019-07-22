package com.zyy.pinyougou.seckill.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.common.pojo.MessageInfo;
import com.zyy.pinyougou.mapper.TbSeckillOrderMapper;
import com.zyy.pinyougou.pay.service.WeixinPayService;
import com.zyy.pinyougou.pojo.TbSeckillOrder;
import com.zyy.pinyougou.seckill.service.SeckillOrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Reference
    private WeixinPayService weixinPayService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    byte[] body = msg.getBody();
                    String str = new String(body);
                    MessageInfo info = JSON.parseObject(str, MessageInfo.class);

                    if (info.getMethod() == MessageInfo.METHOD_UPDATE) {
                        TbSeckillOrder seckillOrder = JSON.parseObject(info.getContext().toString(), TbSeckillOrder.class);
                        //查询数据库，判断订单状态
                        TbSeckillOrder order = seckillOrderMapper.selectByPrimaryKey(seckillOrder.getId());
                        if (order == null) {
                            //关闭微信订单
                            weixinPayService.closePay(seckillOrder.getId() + "");
                            //删除redis数据
                            seckillOrderService.deleteOrder(seckillOrder.getUserId());
                        }
                    }

                }
            }


            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}

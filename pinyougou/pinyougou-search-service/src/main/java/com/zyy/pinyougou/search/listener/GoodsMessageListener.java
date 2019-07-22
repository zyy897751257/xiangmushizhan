package com.zyy.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.common.pojo.MessageInfo;
import com.zyy.pinyougou.pojo.TbItem;
import com.zyy.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService dao;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        System.out.println(">>>>>>>>>>>>>>>>>>接收数据");

        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    switch (messageInfo.getMethod()) {
                        case 1:{
                            String str1 = messageInfo.getContext().toString();
                            List<TbItem> tbItemList = JSON.parseArray(str1, TbItem.class);
                            dao.updateIndex(tbItemList);
                            break;
                        }
                        case 2:{
                            String str1 = messageInfo.getContext().toString();
                            List<TbItem> tbItemList = JSON.parseArray(str1, TbItem.class);
                            dao.updateIndex(tbItemList);
                            break;
                        }
                        case 3:{
                            String str1 = messageInfo.getContext().toString();
                            Long[] longs = JSON.parseObject(str1, Long[].class);
                            dao.deleteByIds(longs);
                            break;
                        }
                        default:
                            break;
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

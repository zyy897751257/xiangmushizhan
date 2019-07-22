package com.zyy.sms.listener;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.zyy.sms.utils.SmsUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SMSMessageListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    Map<String,String> map = JSON.parseObject(s, Map.class);
                    SmsUtil.sendSms(map);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (ClientException e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}

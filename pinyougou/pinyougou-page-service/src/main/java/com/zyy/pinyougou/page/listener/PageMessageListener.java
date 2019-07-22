package com.zyy.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.common.pojo.MessageInfo;
import com.zyy.pinyougou.page.service.ItemPageService;
import com.zyy.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        System.out.println(">>>>当前的线程>>>>" + Thread.currentThread().getName());

        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    byte[] body = msg.getBody();
                    String str = new String(body);
                    MessageInfo info = JSON.parseObject(str, MessageInfo.class);
                    switch (info.getMethod()) {
                        case MessageInfo.METHOD_ADD: //新增
                        {
                            updatePageHtml(info);
                            break;
                        }
                        case MessageInfo.METHOD_UPDATE: //更新
                        {
                            updatePageHtml(info);
                            break;
                        }
                        case MessageInfo.METHOD_DELETE: //删除
                        {
                            String s = info.getContext().toString();
                            //获取Long数组
                            Long[] longs = JSON.parseObject(s, Long[].class);
                            itemPageService.deleteById(longs);
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

    private void updatePageHtml(MessageInfo info) {
        String string = info.getContext().toString();
        List<TbItem> tbItemList = JSON.parseArray(string, TbItem.class);
        Set<Long> set = new HashSet();
        for (TbItem tbItem : tbItemList) {
            set.add(tbItem.getGoodsId());
        }
        for (Long aLong : set) {
            itemPageService.genItemHtml(aLong);
        }

    }
}

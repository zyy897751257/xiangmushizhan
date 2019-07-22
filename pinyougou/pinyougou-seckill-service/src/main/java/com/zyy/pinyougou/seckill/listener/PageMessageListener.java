package com.zyy.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.common.pojo.MessageInfo;
import com.zyy.pinyougou.mapper.TbSeckillGoodsMapper;
import com.zyy.pinyougou.pojo.TbSeckillGoods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageMessageListener implements MessageListenerConcurrently {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            for (MessageExt msg : msgs) {
                byte[] body = msg.getBody();
                MessageInfo info = JSON.parseObject(body, MessageInfo.class);
                Long[] ids = JSON.parseObject(info.getContext().toString(), Long[].class);
                if (info.getMethod() == MessageInfo.METHOD_ADD) {
                    for (Long id : ids) {
                        genHTML("item.ftl",id);
                    }
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    private void genHTML(String templateName, Long id) {
        Writer out = null;
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate(templateName);
            Map model = new HashMap();
            TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
            model.put("seckillGoods",tbSeckillGoods);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(pageDir + tbSeckillGoods.getId() + ".html")), "UTF-8"));
            template.process(model,out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

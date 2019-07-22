package com.zyy.pinyougou.es;

import com.zyy.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Import {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-es.xml");
        ItemService itemService = context.getBean(ItemService.class);
        itemService.ImportDataToEs();
    }
}

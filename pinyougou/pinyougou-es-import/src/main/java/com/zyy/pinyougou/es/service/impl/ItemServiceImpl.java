package com.zyy.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.es.dao.ItemDao;
import com.zyy.pinyougou.es.service.ItemService;
import com.zyy.pinyougou.mapper.TbItemMapper;
import com.zyy.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-06-29 11:01
 * @description:
 * @version:
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public void ImportDataToEs() {
        TbItem record = new TbItem();
        record.setStatus("1");
        List<TbItem> tbItemList = tbItemMapper.select(record);
        for (TbItem tbItem : tbItemList) {
            String spec = tbItem.getSpec();
            if (StringUtils.isNotBlank(spec)) {
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(map);
            }

        }

        itemDao.saveAll(tbItemList);

    }
}

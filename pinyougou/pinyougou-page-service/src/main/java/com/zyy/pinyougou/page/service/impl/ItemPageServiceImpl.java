package com.zyy.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.zyy.pinyougou.mapper.TbGoodsDescMapper;
import com.zyy.pinyougou.mapper.TbGoodsMapper;
import com.zyy.pinyougou.mapper.TbItemCatMapper;
import com.zyy.pinyougou.mapper.TbItemMapper;
import com.zyy.pinyougou.page.service.ItemPageService;
import com.zyy.pinyougou.pojo.TbGoods;
import com.zyy.pinyougou.pojo.TbGoodsDesc;
import com.zyy.pinyougou.pojo.TbItem;
import com.zyy.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-07-03 10:28
 * @description:
 * @version:
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Value("${pageDir}")
    private String pageDir;

    @Override
    public void genItemHtml(Long goodsId) {

        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

        //使用freemarker 创建模板  使用数据集 生成静态页面 (数据集 和模板)
        genHTML("item.ftl", tbGoods, tbGoodsDesc);
    }

    @Override
    public void deleteById(Long[] longs) {
        for (Long aLong : longs) {
            try {
                FileUtils.forceDelete(new File(pageDir+aLong+".html"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void genHTML(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {

        //FileWriter fileWriter = null;
        Writer out = null;
        try {
            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate(templateName);
            Map model = new HashMap();
            model.put("tbGoods", tbGoods);
            model.put("tbGoodsDesc", tbGoodsDesc);
            //根据分类的ID 查询分类的对象
            TbItemCat tbItemCat1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            model.put("tbItemCat1",tbItemCat1.getName());
            model.put("tbItemCat2",tbItemCat2.getName());
            model.put("tbItemCat3",tbItemCat3.getName());
            //fileWriter = new FileWriter(new File(pageDir + tbGoods.getId() + ".html"));
            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId",tbGoods.getId());
            criteria.andEqualTo("status","1");
            example.setOrderByClause("is_default desc");
            List<TbItem> tbItemList = tbItemMapper.selectByExample(example);
            model.put("skuList",tbItemList);

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(pageDir + tbGoods.getId() + ".html")), "UTF-8"));

            template.process(model, out);
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

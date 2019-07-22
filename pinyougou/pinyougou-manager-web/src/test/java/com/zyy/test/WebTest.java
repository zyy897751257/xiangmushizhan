package com.zyy.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyy.pinyougou.pojo.TbBrand;
import com.zyy.pinyougou.sellergoods.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:springmvc.xml")
public class WebTest {

    /*@Reference
    private BrandService brandService;

    @Reference
    private ItemPageService itemPageService;

    @Test
    public void test01() {
        List<TbBrand> all = brandService.findAll();
        for (TbBrand tbBrand : all) {
            System.out.println(tbBrand);
        }
    }

    @Test
    public void test02() {
        itemPageService.genItemHtml(149187842867981L);
    }*/
}

package com.zyy.pinyougou.page.service;

/**
 * @author: Zyy
 * @date: 2019-07-03 10:00
 * @description:
 * @version:
 */
public interface ItemPageService {

    /**
     * 生成商品详细页
     * @param goodsId
     */
    public void genItemHtml(Long goodsId);

    void deleteById(Long[] longs);

}

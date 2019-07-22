package com.zyy.pinyougou.search.service;

import com.zyy.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-06-29 20:07
 * @description:
 * @version:
 */
public interface ItemSearchService {

    Map<String,Object> search(Map<String, Object> searchMap);

    void updateIndex(List<TbItem> tbItems);

    void deleteByIds(Long[] ids);

}

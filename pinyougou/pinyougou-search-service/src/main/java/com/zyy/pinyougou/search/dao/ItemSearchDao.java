package com.zyy.pinyougou.search.dao;


import com.zyy.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: Zyy
 * @date: 2019-07-02 20:25
 * @description:
 * @version:
 */
public interface ItemSearchDao extends ElasticsearchRepository<TbItem,Long> {

}

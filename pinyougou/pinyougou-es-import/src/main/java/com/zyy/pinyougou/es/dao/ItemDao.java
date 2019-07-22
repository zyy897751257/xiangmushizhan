package com.zyy.pinyougou.es.dao;

import com.zyy.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * @author: Zyy
 * @date: 2019-06-29 12:02
 * @description:
 * @version:
 */
public interface ItemDao extends ElasticsearchCrudRepository<TbItem,Long> {
}

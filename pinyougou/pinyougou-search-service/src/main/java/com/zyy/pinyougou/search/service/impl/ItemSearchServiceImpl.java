package com.zyy.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zyy.pinyougou.pojo.TbItem;
import com.zyy.pinyougou.search.dao.ItemSearchDao;
import com.zyy.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-06-29 20:08
 * @description:
 * @version:
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemSearchDao itemSearchDao;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        //出现异常
        Assert.notNull(searchMap,"object 不能为空");

        Map<String, Object> resultMap = new HashMap<>();

        String keywords = (String) searchMap.get("keywords");
        String category = (String) searchMap.get("category");
        String brand = (String) searchMap.get("brand");
        Map<String,String> specMap = (Map) searchMap.get("spec");
        String price = (String) searchMap.get("price");
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 40;
        }

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //searchQueryBuilder.withQuery(QueryBuilders.matchQuery("keyword", keywords));

        //判断关键字是否为空
        if (StringUtils.isNotBlank(keywords)) {

            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"title","seller","brand","category"));

            //聚合分组
            searchQueryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category"));

        } else {
            //匹配所有
            searchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(category)) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }

        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }

        if (StringUtils.isNotBlank(price)) {
            String[] strings = price.split("-");
            if ("*".equals(strings[1])) {
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(strings[1]));
            } else {
                boolQuery.filter(QueryBuilders.rangeQuery("price").from(strings[0],true).to(strings[1], true));
            }

        }

        if (specMap != null) {
            for (String key : specMap.keySet()) {
                boolQuery.filter(QueryBuilders.termQuery("specMap."+key+".keyword", specMap.get(key)));
            }
        }

        //设置高亮
        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title"))
                          .withHighlightBuilder(new HighlightBuilder()
                                  .preTags("<em style='color:red'>")
                                  .postTags("</em>"));

        searchQueryBuilder.withFilter(boolQuery);

        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        //排序
        String sortField = (String) searchMap.get("sortField");
        String sortType = (String) searchMap.get("sortType");

        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            if ("ASC".equals(sortType)) {
                searchQuery.addSort(new Sort(Sort.Direction.ASC,sortField));
            } else if ("DESC".equals(sortType)) {
                searchQuery.addSort(new Sort(Sort.Direction.DESC,sortField));
            } else {
                System.out.println("不排序");
            }
        }
        //设置分页
        searchQuery.setPageable(PageRequest.of(pageNo-1, pageSize));
        //AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                List<T> content = new ArrayList<>();

                //判断是否搜索到记录
                if (hits == null || hits.getHits().length <=0) {
                    return new AggregatedPageImpl(content);
                }
                for (SearchHit hit : hits) {
                    String sourceAsString = hit.getSourceAsString();
                    TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);

                    //获取高亮
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    //获取高亮的域为title的高亮对象
                    HighlightField highlightField = highlightFields.get("title");

                    if (highlightField != null) {
                        //获取高亮碎片
                        Text[] fragments = highlightField.getFragments();
                        StringBuffer buffer = new StringBuffer();

                        if (fragments != null) {
                            for (Text fragment : fragments) {
                                buffer.append(fragment.toString());
                            }
                        }
                        //判断buffer是否为空
                        if (StringUtils.isNotBlank(buffer.toString())) {
                            tbItem.setTitle(buffer.toString());
                        }
                    }

                    content.add((T) tbItem);
                }

                AggregatedPageImpl<T> aggregatedPage = new AggregatedPageImpl<>(content, pageable, hits.getTotalHits(), searchResponse.getAggregations(), searchResponse.getScrollId());

                return aggregatedPage;
            }
        });

        //获取分组结果
        Aggregation category_group = tbItems.getAggregation("category_group");
        StringTerms terms = (StringTerms) category_group;

        //商品分类分组
        List<String> categoryList = new ArrayList<>();

        if (terms != null) {
            for (StringTerms.Bucket bucket : terms.getBuckets()) {
                categoryList.add(bucket.getKeyAsString());
            }
        }

        if (StringUtils.isBlank(category)) {
            //默认查找第一个分类下的品牌和规格
            if (categoryList.size() > 0) {
                Map map = searchBrandAndSpecList(categoryList.get(0));
                resultMap.putAll(map);
            }
        } else {
            //查找选中分类下的品牌和规格
            Map map = searchBrandAndSpecList(category);
            resultMap.putAll(map);
        }



        List<TbItem> tbItemList = tbItems.getContent();
        int totalPages = tbItems.getTotalPages();
        long totalElements = tbItems.getTotalElements();

        resultMap.put("rows", tbItemList);
        resultMap.put("totalPages", totalPages);
        resultMap.put("total", totalElements);
        resultMap.put("categoryList",categoryList);

        return resultMap;

    }

    @Override
    public void updateIndex(List<TbItem> tbItems) {
        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }

        itemSearchDao.saveAll(tbItems);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termQuery("goodsId", ids));

        elasticsearchTemplate.delete(deleteQuery);
    }

    private Map searchBrandAndSpecList(String category) {

        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);

            map.put("brandList",brandList);
            map.put("specList",specList);
        }

        return map;
    }
}

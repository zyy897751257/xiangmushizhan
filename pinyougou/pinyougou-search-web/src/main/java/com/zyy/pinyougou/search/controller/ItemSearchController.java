package com.zyy.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyy.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Zyy
 * @date: 2019-06-29 20:29
 * @description:
 * @version:
 */
@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map<String,Object> searchMap){
        if(searchMap==null){
            searchMap=new HashMap<>();
        }
        return itemSearchService.search(searchMap);
    }

}

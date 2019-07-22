package com.zyy.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired; 
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.zyy.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.zyy.pinyougou.mapper.TbOrderItemMapper;
import com.zyy.pinyougou.pojo.TbOrderItem;

import com.zyy.pinyougou.sellergoods.service.OrderItemService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderItemServiceImpl extends CoreServiceImpl<TbOrderItem>  implements OrderItemService {

	
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	public OrderItemServiceImpl(TbOrderItemMapper orderItemMapper) {
		super(orderItemMapper, TbOrderItem.class);
		this.orderItemMapper=orderItemMapper;
	}

	
	

	
	@Override
    public PageInfo<TbOrderItem> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbOrderItem> all = orderItemMapper.selectAll();
        PageInfo<TbOrderItem> info = new PageInfo<TbOrderItem>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrderItem> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbOrderItem> findPage(Integer pageNo, Integer pageSize, TbOrderItem orderItem) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbOrderItem.class);
        Example.Criteria criteria = example.createCriteria();

        if(orderItem!=null){			
						if(StringUtils.isNotBlank(orderItem.getTitle())){
				criteria.andLike("title","%"+orderItem.getTitle()+"%");
				//criteria.andTitleLike("%"+orderItem.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(orderItem.getPicPath())){
				criteria.andLike("picPath","%"+orderItem.getPicPath()+"%");
				//criteria.andPicPathLike("%"+orderItem.getPicPath()+"%");
			}
			if(StringUtils.isNotBlank(orderItem.getSellerId())){
				criteria.andLike("sellerId","%"+orderItem.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+orderItem.getSellerId()+"%");
			}
	
		}
        List<TbOrderItem> all = orderItemMapper.selectByExample(example);
        PageInfo<TbOrderItem> info = new PageInfo<TbOrderItem>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrderItem> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}

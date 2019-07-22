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

import com.zyy.pinyougou.mapper.TbGoodsDescMapper;
import com.zyy.pinyougou.pojo.TbGoodsDesc;

import com.zyy.pinyougou.sellergoods.service.GoodsDescService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsDescServiceImpl extends CoreServiceImpl<TbGoodsDesc>  implements GoodsDescService {

	
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	public GoodsDescServiceImpl(TbGoodsDescMapper goodsDescMapper) {
		super(goodsDescMapper, TbGoodsDesc.class);
		this.goodsDescMapper=goodsDescMapper;
	}

	
	

	
	@Override
    public PageInfo<TbGoodsDesc> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbGoodsDesc> all = goodsDescMapper.selectAll();
        PageInfo<TbGoodsDesc> info = new PageInfo<TbGoodsDesc>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoodsDesc> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbGoodsDesc> findPage(Integer pageNo, Integer pageSize, TbGoodsDesc goodsDesc) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbGoodsDesc.class);
        Example.Criteria criteria = example.createCriteria();

        if(goodsDesc!=null){			
						if(StringUtils.isNotBlank(goodsDesc.getIntroduction())){
				criteria.andLike("introduction","%"+goodsDesc.getIntroduction()+"%");
				//criteria.andIntroductionLike("%"+goodsDesc.getIntroduction()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getSpecificationItems())){
				criteria.andLike("specificationItems","%"+goodsDesc.getSpecificationItems()+"%");
				//criteria.andSpecificationItemsLike("%"+goodsDesc.getSpecificationItems()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+goodsDesc.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+goodsDesc.getCustomAttributeItems()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getItemImages())){
				criteria.andLike("itemImages","%"+goodsDesc.getItemImages()+"%");
				//criteria.andItemImagesLike("%"+goodsDesc.getItemImages()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getPackageList())){
				criteria.andLike("packageList","%"+goodsDesc.getPackageList()+"%");
				//criteria.andPackageListLike("%"+goodsDesc.getPackageList()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getSaleService())){
				criteria.andLike("saleService","%"+goodsDesc.getSaleService()+"%");
				//criteria.andSaleServiceLike("%"+goodsDesc.getSaleService()+"%");
			}
	
		}
        List<TbGoodsDesc> all = goodsDescMapper.selectByExample(example);
        PageInfo<TbGoodsDesc> info = new PageInfo<TbGoodsDesc>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoodsDesc> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}

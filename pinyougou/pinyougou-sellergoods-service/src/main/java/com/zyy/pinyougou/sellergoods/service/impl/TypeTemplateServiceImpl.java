package com.zyy.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.zyy.pinyougou.mapper.TbSpecificationOptionMapper;
import com.zyy.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.zyy.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.zyy.pinyougou.mapper.TbTypeTemplateMapper;
import com.zyy.pinyougou.pojo.TbTypeTemplate;

import com.zyy.pinyougou.sellergoods.service.TypeTemplateService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate>  implements TypeTemplateService {

	
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
		super(typeTemplateMapper, TbTypeTemplate.class);
		this.typeTemplateMapper=typeTemplateMapper;
	}

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if(typeTemplate!=null){			
						if(StringUtils.isNotBlank(typeTemplate.getName())){
				criteria.andLike("name","%"+typeTemplate.getName()+"%");
				//criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getSpecIds())){
				criteria.andLike("specIds","%"+typeTemplate.getSpecIds()+"%");
				//criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getBrandIds())){
				criteria.andLike("brandIds","%"+typeTemplate.getBrandIds()+"%");
				//criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+typeTemplate.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);

		 List<TbTypeTemplate> typeTemplateList = findAll();
		 for (TbTypeTemplate tbTypeTemplate : typeTemplateList) {
			 List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
			 redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);

			 List<Map> specList = findSpecList(tbTypeTemplate.getId());
			 redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
		 }

		 return pageInfo;
    }

    public List<Map> findSpecList(Long typeTemplateId) {
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(typeTemplateId);
		String specIds = tbTypeTemplate.getSpecIds();
		List<Map> maps = JSON.parseArray(specIds, Map.class);
		for (Map map : maps) {
			Integer specid = (Integer) map.get("id");
			TbSpecificationOption option = new TbSpecificationOption();
			option.setSpecId(Long.valueOf(specid));
			List<TbSpecificationOption> optionList = tbSpecificationOptionMapper.select(option);
			map.put("options",optionList);
		}

		return maps;
	}

	@Override
	public TbTypeTemplate findOneAndSpecIds(Long id) {
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);

		String specIds = tbTypeTemplate.getSpecIds();
		List<Map> specMaps = JSON.parseArray(specIds, Map.class);
		for (Map map : specMaps) {
			Integer specId = (Integer) map.get("id");
			TbSpecificationOption option = new TbSpecificationOption();
			option.setSpecId(Long.valueOf(specId));
			List<TbSpecificationOption> optionList = tbSpecificationOptionMapper.select(option);
			map.put("options", optionList);
		}

		String jsonStr = JSON.toJSONString(specMaps);
		tbTypeTemplate.setSpecIds(jsonStr);
		return tbTypeTemplate;
	}

}

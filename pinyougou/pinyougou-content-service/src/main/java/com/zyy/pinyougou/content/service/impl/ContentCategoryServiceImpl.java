package com.zyy.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.content.service.ContentCategoryService;
import com.zyy.pinyougou.core.service.CoreServiceImpl;
import com.zyy.pinyougou.mapper.TbContentCategoryMapper;
import com.zyy.pinyougou.pojo.TbContentCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentCategoryServiceImpl extends CoreServiceImpl<TbContentCategory>  implements ContentCategoryService {

	
	private TbContentCategoryMapper contentCategoryMapper;

	@Autowired
	public ContentCategoryServiceImpl(TbContentCategoryMapper contentCategoryMapper) {
		super(contentCategoryMapper, TbContentCategory.class);
		this.contentCategoryMapper=contentCategoryMapper;
	}

	
	

	
	@Override
    public PageInfo<TbContentCategory> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbContentCategory> all = contentCategoryMapper.selectAll();
        PageInfo<TbContentCategory> info = new PageInfo<TbContentCategory>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContentCategory> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbContentCategory> findPage(Integer pageNo, Integer pageSize, TbContentCategory contentCategory) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbContentCategory.class);
        Example.Criteria criteria = example.createCriteria();

        if(contentCategory!=null){			
						if(StringUtils.isNotBlank(contentCategory.getName())){
				criteria.andLike("name","%"+contentCategory.getName()+"%");
				//criteria.andNameLike("%"+contentCategory.getName()+"%");
			}
			if(StringUtils.isNotBlank(contentCategory.getContentGroup())){
				criteria.andLike("contentGroup","%"+contentCategory.getContentGroup()+"%");
				//criteria.andContentGroupLike("%"+contentCategory.getContentGroup()+"%");
			}
			if(StringUtils.isNotBlank(contentCategory.getContentKey())){
				criteria.andLike("contentKey","%"+contentCategory.getContentKey()+"%");
				//criteria.andContentKeyLike("%"+contentCategory.getContentKey()+"%");
			}
			if(StringUtils.isNotBlank(contentCategory.getStatus())){
				criteria.andLike("status","%"+contentCategory.getStatus()+"%");
				//criteria.andStatusLike("%"+contentCategory.getStatus()+"%");
			}
	
		}
        List<TbContentCategory> all = contentCategoryMapper.selectByExample(example);
        PageInfo<TbContentCategory> info = new PageInfo<TbContentCategory>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContentCategory> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}

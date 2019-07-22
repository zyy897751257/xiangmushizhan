package com.zyy.pinyougou.sellergoods.service;
import com.zyy.pinyougou.pojo.TbTypeTemplate;

import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface TypeTemplateService extends CoreService<TbTypeTemplate> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate TypeTemplate);

    TbTypeTemplate findOneAndSpecIds(Long id);
}

package com.zyy.pinyougou.content.service;

import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.core.service.CoreService;
import com.zyy.pinyougou.pojo.TbContent;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ContentService extends CoreService<TbContent> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent Content);

	List<TbContent> findByCategoryId(Long categoryId);

}

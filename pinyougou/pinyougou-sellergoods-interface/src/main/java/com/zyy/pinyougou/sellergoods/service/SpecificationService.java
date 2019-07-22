package com.zyy.pinyougou.sellergoods.service;
import java.util.List;

import com.zyy.pinyougou.entity.Specification;
import com.zyy.pinyougou.pojo.TbSpecification;

import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification Specification);


	void add(Specification specification);

	Specification findOne(Long id);

	void update(Specification specification);

	void delete(Long[] ids);

}

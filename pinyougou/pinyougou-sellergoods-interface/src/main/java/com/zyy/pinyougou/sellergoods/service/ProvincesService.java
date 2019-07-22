package com.zyy.pinyougou.sellergoods.service;
import java.util.List;
import com.zyy.pinyougou.pojo.TbProvinces;

import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ProvincesService extends CoreService<TbProvinces> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbProvinces> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbProvinces> findPage(Integer pageNo, Integer pageSize, TbProvinces Provinces);
	
}

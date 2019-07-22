package com.zyy.pinyougou.sellergoods.service;
import java.util.List;

import com.zyy.pinyougou.pojo.Goods;
import com.zyy.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.core.service.CoreService;
import com.zyy.pinyougou.pojo.TbItem;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);

	void add(Goods goods);

	Goods findOne(Long id);

	void update(Goods goods);

	void updateStatus(Long[] ids, String statusId);

	void delete(Long[] ids);

	List<TbItem> findTbItemListByIds(Long[] ids);
}

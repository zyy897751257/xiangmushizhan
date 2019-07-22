package com.zyy.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.common.pojo.MessageInfo;
import com.zyy.pinyougou.entity.Result;
import com.zyy.pinyougou.pojo.Goods;
import com.zyy.pinyougou.pojo.TbGoods;
import com.zyy.pinyougou.pojo.TbItem;
import com.zyy.pinyougou.sellergoods.service.GoodsService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private DefaultMQProducer mqProducer;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
									  @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(username);
		goods.getTbGoods().setSellerId(username);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);
			MessageInfo messageInfo = new MessageInfo(ids, "Goods_Topic", "goods_delete_tag", "delete", MessageInfo.METHOD_DELETE);
			SendResult sendResult = mqProducer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));

			System.out.println(">>>>"+sendResult.getSendStatus());
			//itemSearchService.deleteByIds(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {

        return goodsService.findPage(pageNo, pageSize, goods);
    }

    @RequestMapping("/updateStatus/{statusId}")
    public Result updateStatus(@RequestBody Long[] ids,@PathVariable("statusId") String statusId) {
		try {
			goodsService.updateStatus(ids,statusId);

			if ("1".equals(statusId)) {
				List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);

				MessageInfo messageInfo = new MessageInfo(tbItemList, "Goods_Topic", "goods_update_tag", "updateStatus",MessageInfo.METHOD_UPDATE);

				SendResult sendResult = mqProducer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
				System.out.println(">>>>"+sendResult.getSendStatus());

				/*itemSearchService.updateIndex(tbItemList);
				for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}*/

			}

			return new Result(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "审核失败");
		}
	}
	
}

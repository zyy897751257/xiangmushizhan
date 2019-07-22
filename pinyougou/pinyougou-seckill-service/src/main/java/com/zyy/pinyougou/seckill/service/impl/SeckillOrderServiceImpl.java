package com.zyy.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.common.IdWorker;
import com.zyy.pinyougou.common.SysConstants;
import com.zyy.pinyougou.core.service.CoreServiceImpl;
import com.zyy.pinyougou.mapper.TbSeckillGoodsMapper;
import com.zyy.pinyougou.mapper.TbSeckillOrderMapper;
import com.zyy.pinyougou.pojo.TbSeckillGoods;
import com.zyy.pinyougou.pojo.TbSeckillOrder;
import com.zyy.pinyougou.seckill.pojo.SeckillStatus;
import com.zyy.pinyougou.seckill.service.SeckillOrderService;
import com.zyy.pinyougou.seckill.thread.CreateOrderThread;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder>  implements SeckillOrderService {

	
	private TbSeckillOrderMapper seckillOrderMapper;

	@Autowired
	public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
		super(seckillOrderMapper, TbSeckillOrder.class);
		this.seckillOrderMapper=seckillOrderMapper;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;

	@Autowired
	private CreateOrderThread orderThread;

	@Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	 @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(seckillOrder!=null){			
						if(StringUtils.isNotBlank(seckillOrder.getUserId())){
				criteria.andLike("userId","%"+seckillOrder.getUserId()+"%");
				//criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getSellerId())){
				criteria.andLike("sellerId","%"+seckillOrder.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getStatus())){
				criteria.andLike("status","%"+seckillOrder.getStatus()+"%");
				//criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverAddress())){
				criteria.andLike("receiverAddress","%"+seckillOrder.getReceiverAddress()+"%");
				//criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+seckillOrder.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiver())){
				criteria.andLike("receiver","%"+seckillOrder.getReceiver()+"%");
				//criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getTransactionId())){
				criteria.andLike("transactionId","%"+seckillOrder.getTransactionId()+"%");
				//criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void submitOrder(Long id, String userId) {

		if (redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId) != null) {
			throw new RuntimeException("已在排队中");
		}

		if (redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId) != null) {
			throw new RuntimeException("有未支付订单，请先支付");
		}

		Long goodId = (Long) redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + id).rightPop();

		if (goodId == null) {
			throw new RuntimeException("商品已被抢光");
		}

		redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(new SeckillStatus(userId,id,SeckillStatus.SECKILL_queuing));

		redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId, id);

		orderThread.handlerOrder();
	}

	@Override
	public TbSeckillOrder getUserOrderStatus(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
	}

	@Override
	public void updateOrderStatus(String userId, String transaction_id) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
		if (seckillOrder != null) {
			seckillOrder.setPayTime(new Date());
			seckillOrder.setStatus("1");
			seckillOrder.setTransactionId(transaction_id);

			seckillOrderMapper.insert(seckillOrder);

			//删除redis中的数据
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
		}
	}

	@Override
	public void deleteOrder(String userId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);

		if (seckillOrder == null) {
			System.out.println("没有该订单");
			return;
		}
		//删除订单
		Long seckillId = seckillOrder.getSeckillId();
		//从redis中获取商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);

		if (seckillGoods == null) {
			//redis中没有该商品数据
			seckillGoods = tbSeckillGoodsMapper.selectByPrimaryKey(seckillId);
			seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
			//存到redis中
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,seckillGoods);
		} else {
			//直接在redis中恢复库存
			seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
			//存到redis中
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,seckillGoods);
		}

		//恢复队列
		redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).leftPush(seckillId);
		//删除预订单
		redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
	}
}

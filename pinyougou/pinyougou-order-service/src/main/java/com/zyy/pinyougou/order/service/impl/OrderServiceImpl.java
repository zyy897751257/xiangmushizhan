package com.zyy.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.common.IdWorker;
import com.zyy.pinyougou.core.service.CoreServiceImpl;
import com.zyy.pinyougou.entity.Cart;
import com.zyy.pinyougou.mapper.TbItemMapper;
import com.zyy.pinyougou.mapper.TbOrderItemMapper;
import com.zyy.pinyougou.mapper.TbOrderMapper;
import com.zyy.pinyougou.mapper.TbPayLogMapper;
import com.zyy.pinyougou.order.service.OrderService;
import com.zyy.pinyougou.pojo.TbItem;
import com.zyy.pinyougou.pojo.TbOrder;
import com.zyy.pinyougou.pojo.TbOrderItem;
import com.zyy.pinyougou.pojo.TbPayLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder>  implements OrderService {
	
	private TbOrderMapper orderMapper;

	@Autowired
	public OrderServiceImpl(TbOrderMapper orderMapper) {
		super(orderMapper, TbOrder.class);
		this.orderMapper=orderMapper;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;

	@Autowired
	private TbPayLogMapper tbPayLogMapper;

	@Override
	public void add(TbOrder order) {
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		double total_money = 0;
		List<Long> orderList =new ArrayList<>();
		for (Cart cart : cartList) {
			long orderId = idWorker.nextId();
			orderList.add(orderId);
			System.out.println("sellerId:" + cart.getSellerId());
			TbOrder tborder=new TbOrder();//新创建订单对象
			tborder.setOrderId(orderId);//订单ID
			tborder.setUserId(order.getUserId());//用户名
			tborder.setPaymentType(order.getPaymentType());//支付类型
			tborder.setStatus("1");//状态：未付款
			tborder.setCreateTime(new Date());//订单创建日期
			tborder.setUpdateTime(new Date());//订单更新日期
			tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
			tborder.setReceiverMobile(order.getReceiverMobile());//手机号
			tborder.setReceiver(order.getReceiver());//收货人
			tborder.setSourceType(order.getSourceType());//订单来源
			tborder.setSellerId(cart.getSellerId());//商家ID
			//循环购物车，获取金额
			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);
				orderItem.setSellerId(cart.getSellerId());
				TbItem item = tbItemMapper.selectByPrimaryKey(orderItem.getItemId());
				orderItem.setGoodsId(item.getGoodsId());

				money += orderItem.getTotalFee().doubleValue();
				tbOrderItemMapper.insert(orderItem);
			}
			tborder.setPayment(new BigDecimal(money));

			total_money += money;
			orderMapper.insert(tborder);
		}
		//支付日志
		TbPayLog tbPayLog = new TbPayLog();
		String outTradeNo = idWorker.nextId() + "";
		tbPayLog.setOutTradeNo(outTradeNo);//支付订单号
		tbPayLog.setCreateTime(new Date());//创建时间
		tbPayLog.setPayType("1");//支付类型
		String ids = orderList.toString().replace("[", "").replace("]", "").replace("", "");
		tbPayLog.setOrderList(ids);
		tbPayLog.setTotalFee((long) (total_money*100));
		tbPayLog.setTradeState("0");
		tbPayLog.setUserId(order.getUserId());
		tbPayLogMapper.insert(tbPayLog);
		redisTemplate.boundHashOps("payLog").put(order.getUserId(), tbPayLog);
		//删除redis中的购物车数据
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
	}

	@Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	@Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(order!=null){
						if(StringUtils.isNotBlank(order.getPaymentType())){
				criteria.andLike("paymentType","%"+order.getPaymentType()+"%");
				//criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(StringUtils.isNotBlank(order.getPostFee())){
				criteria.andLike("postFee","%"+order.getPostFee()+"%");
				//criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(StringUtils.isNotBlank(order.getStatus())){
				criteria.andLike("status","%"+order.getStatus()+"%");
				//criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingName())){
				criteria.andLike("shippingName","%"+order.getShippingName()+"%");
				//criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingCode())){
				criteria.andLike("shippingCode","%"+order.getShippingCode()+"%");
				//criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getUserId())){
				criteria.andLike("userId","%"+order.getUserId()+"%");
				//criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerMessage())){
				criteria.andLike("buyerMessage","%"+order.getBuyerMessage()+"%");
				//criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerNick())){
				criteria.andLike("buyerNick","%"+order.getBuyerNick()+"%");
				//criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerRate())){
				criteria.andLike("buyerRate","%"+order.getBuyerRate()+"%");
				//criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverAreaName())){
				criteria.andLike("receiverAreaName","%"+order.getReceiverAreaName()+"%");
				//criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+order.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverZipCode())){
				criteria.andLike("receiverZipCode","%"+order.getReceiverZipCode()+"%");
				//criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiver())){
				criteria.andLike("receiver","%"+order.getReceiver()+"%");
				//criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(order.getInvoiceType())){
				criteria.andLike("invoiceType","%"+order.getInvoiceType()+"%");
				//criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSourceType())){
				criteria.andLike("sourceType","%"+order.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSellerId())){
				criteria.andLike("sellerId","%"+order.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}

		}
        List<TbOrder> all = orderMapper.selectByExample(example);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//修改支付日志状态
		TbPayLog payLog = tbPayLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());
		payLog.setTradeState("1");
		payLog.setTransactionId(transaction_id);
		tbPayLogMapper.updateByPrimaryKey(payLog);
		//修改订单状态
		String orderList = payLog.getOrderList();
		String[] orderIds = orderList.split(",");

		for (String orderId : orderIds) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(orderId);
			if (tbOrder != null) {
				tbOrder.setStatus("2");
				orderMapper.updateByPrimaryKey(tbOrder);
			}
		}
		//清楚redis缓存
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
	}

}

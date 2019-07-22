package com.zyy.pinyougou.seckill.task;

import com.zyy.pinyougou.common.SysConstants;
import com.zyy.pinyougou.mapper.TbSeckillGoodsMapper;
import com.zyy.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class GoodsTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/15 * * * * ?")
    public void pushGoods() {
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");//审核通过的
        criteria.andGreaterThan("stockCount",0);//大于0
        Date date = new Date();
        criteria.andLessThan("startTime",date);
        criteria.andGreaterThan("endTime",date);

        //排除 已经在redis中的商品
        Set<Long> keys = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();

        if (keys != null && keys.size() > 0) {
            criteria.andNotIn("id",keys);
        }

        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(example);
        for (TbSeckillGoods good : goods) {
            pushGoodsList(good);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(good.getId(),good);
        }
    }

    private void pushGoodsList(TbSeckillGoods good) {
        for (Integer i = 0; i < good.getStockCount(); i++) {

            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + good.getId()).leftPush(good.getId());
        }
    }
}

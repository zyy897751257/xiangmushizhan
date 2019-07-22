package com.zyy.pinyougou.user.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zyy.pinyougou.user.service.UserService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.zyy.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.zyy.pinyougou.mapper.TbUserMapper;
import com.zyy.pinyougou.pojo.TbUser;




/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser>  implements UserService {

	private TbUserMapper userMapper;

	@Autowired
	public UserServiceImpl(TbUserMapper userMapper) {
		super(userMapper, TbUser.class);
		this.userMapper=userMapper;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private DefaultMQProducer mqProducer;

	@Value("${template_code}")
	private String templateCode;

	@Value("${sign_name}")
	private String signName;

	@Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	@Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if(user!=null){			
						if(StringUtils.isNotBlank(user.getUsername())){
				criteria.andLike("username","%"+user.getUsername()+"%");
				//criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(StringUtils.isNotBlank(user.getPassword())){
				criteria.andLike("password","%"+user.getPassword()+"%");
				//criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(StringUtils.isNotBlank(user.getPhone())){
				criteria.andLike("phone","%"+user.getPhone()+"%");
				//criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(StringUtils.isNotBlank(user.getEmail())){
				criteria.andLike("email","%"+user.getEmail()+"%");
				//criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(StringUtils.isNotBlank(user.getSourceType())){
				criteria.andLike("sourceType","%"+user.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(user.getNickName())){
				criteria.andLike("nickName","%"+user.getNickName()+"%");
				//criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(StringUtils.isNotBlank(user.getName())){
				criteria.andLike("name","%"+user.getName()+"%");
				//criteria.andNameLike("%"+user.getName()+"%");
			}
			if(StringUtils.isNotBlank(user.getStatus())){
				criteria.andLike("status","%"+user.getStatus()+"%");
				//criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(user.getHeadPic())){
				criteria.andLike("headPic","%"+user.getHeadPic()+"%");
				//criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(StringUtils.isNotBlank(user.getQq())){
				criteria.andLike("qq","%"+user.getQq()+"%");
				//criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsMobileCheck())){
				criteria.andLike("isMobileCheck","%"+user.getIsMobileCheck()+"%");
				//criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsEmailCheck())){
				criteria.andLike("isEmailCheck","%"+user.getIsEmailCheck()+"%");
				//criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getSex())){
				criteria.andLike("sex","%"+user.getSex()+"%");
				//criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public void createSmsCode(String phone) {
		try {
			Random random = new Random();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < 6; i++) {
				buffer.append(random.nextInt(10));
			}
			String code = buffer.toString();

			//将验证码存在redis中
			redisTemplate.boundHashOps("SmsCode").put(phone, code);

			Map<String, String> map = new HashMap<>();
			map.put("mobile", phone);
			map.put("sign_name", signName);
			map.put("template_code", templateCode);
			map.put("param", "{'code':'"+code+"'}");

			mqProducer.send(new Message("SMS_TOPIC", "SEND_MESSAGE_TAG", "createSmsCode", JSON.toJSONString(map).getBytes()));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean checkSmsCode(String phone, String code) {

		String smsCode = (String) redisTemplate.boundHashOps("SmsCode").get(phone);

		if (smsCode == null) {
			return false;
		}

		if (!smsCode.equals(code)) {
			return false;
		}

		return true;

	}

	/*public static void main(String[] args) {
		//String code =  (long) ((Math.random() * 9) * 100000)+"";
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int j = 0; j < 6; j++) {
			int i = random.nextInt(10);
			buffer.append(i);
		}

		System.out.println(buffer.toString());
	}*/
}

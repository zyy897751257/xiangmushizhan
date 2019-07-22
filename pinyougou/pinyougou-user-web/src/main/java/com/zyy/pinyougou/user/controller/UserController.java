package com.zyy.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.zyy.pinyougou.common.PhoneFormatCheckUtils;
import com.zyy.pinyougou.entity.Error;
import com.zyy.pinyougou.entity.Result;
import com.zyy.pinyougou.pojo.TbUser;
import com.zyy.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
									 @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add/{code}")
	public Result add(@Valid @RequestBody TbUser user, BindingResult bindingResult, @PathVariable("code") String code){
		try {
			//先校验数据
			if (bindingResult.hasErrors()) {
				//有问题
				Result result = new Result(false, "失败");
				List<FieldError> fieldErrors = bindingResult.getFieldErrors();
				for (FieldError fieldError : fieldErrors) {
					result.getErrorsList().add(new Error(fieldError.getField(),fieldError.getDefaultMessage()));

				}
				return result;
			}

			//数据没问题
			boolean flag = userService.checkSmsCode(user.getPhone(), code);
			if (!flag) {
				return new Result(false, "验证码错误");
			}

			user.setCreated(new Date());
			user.setUpdated(new Date());
			user.setPassword(DigestUtils.md5Hex(user.getPassword()));
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }

    @RequestMapping("/sendCode")
    public Result sendSmsCode(String phone) {

		if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {

			try {
				userService.createSmsCode(phone);
				return new Result(true, "验证码发送成功");
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "验证码发送失败");
			}
		}

		return new Result(false, "手机号格式不正确");
	}
	
}

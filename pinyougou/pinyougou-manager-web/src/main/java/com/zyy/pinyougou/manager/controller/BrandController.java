package com.zyy.pinyougou.manager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zyy.pinyougou.common.POIUtils;
import com.zyy.pinyougou.entity.Result;
import com.zyy.pinyougou.pojo.TbBrand;
import com.zyy.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}

	@RequestMapping("/findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return brandService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param brand
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand brand){
		try {
			brandService.add(brand);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param brand
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand brand){
		try {
			brandService.update(brand);
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
	public TbBrand findOne(@PathVariable(value = "id") Long id){
		return brandService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	@RequestMapping("/search")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbBrand brand) {
        return brandService.findPage(pageNo, pageSize, brand);
    }

    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile) {
		try {
			//读取Excel文件数据
			List<String[]> list = POIUtils.readExcel(excelFile);
			if(list != null && list.size() > 0){
				List<TbBrand> brandList = new ArrayList();
				for (String[] strings : list) {
					TbBrand tbBrand = new TbBrand();
					tbBrand.setName(strings[0]);
					tbBrand.setFirstChar(strings[1]);
					brandList.add(tbBrand);
				}
				brandService.add(brandList);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(false, "导入失败");
		}
		return new Result(true,"导入成功");
	}
	
}

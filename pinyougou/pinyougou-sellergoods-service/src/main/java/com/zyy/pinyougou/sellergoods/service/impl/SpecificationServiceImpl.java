package com.zyy.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.zyy.pinyougou.entity.Specification;
import com.zyy.pinyougou.mapper.TbSpecificationOptionMapper;
import com.zyy.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.zyy.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.zyy.pinyougou.mapper.TbSpecificationMapper;
import com.zyy.pinyougou.pojo.TbSpecification;

import com.zyy.pinyougou.sellergoods.service.SpecificationService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl extends CoreServiceImpl<TbSpecification>  implements SpecificationService {

	
	private TbSpecificationMapper specificationMapper;

	@Autowired
	public SpecificationServiceImpl(TbSpecificationMapper specificationMapper) {
		super(specificationMapper, TbSpecification.class);
		this.specificationMapper =specificationMapper;
	}

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	

	
	@Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSpecification> all = specificationMapper.selectAll();
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if(specification!=null){			
						if(StringUtils.isNotBlank(specification.getSpecName())){
				criteria.andLike("specName","%"+specification.getSpecName()+"%");
				//criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void add(Specification specification) {
	    //新增TbSpecification类
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification);
        System.out.println(tbSpecification);
        //新增SpecificationOption
        List<TbSpecificationOption> optionList = specification.getOptionList();
        if (optionList.size() > 0 && optionList != null) {
            for (TbSpecificationOption tbSpecificationOption : optionList) {
                tbSpecificationOption.setSpecId(tbSpecification.getId());
                tbSpecificationOptionMapper.insert(tbSpecificationOption);
            }
        }
    }

    @Override
    public Specification findOne(Long id) {
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(id);
        List<TbSpecificationOption> optionList = tbSpecificationOptionMapper.select(option);
        Specification specification = new Specification();
        specification.setTbSpecification(tbSpecification);
        specification.setOptionList(optionList);
        return specification;
    }

    @Override
    public void update(Specification specification) {
        TbSpecification tbSpecification = specification.getTbSpecification();
        //更新主表
        specificationMapper.updateByPrimaryKey(tbSpecification);
        //更新关联表option
        List<TbSpecificationOption> optionList = specification.getOptionList();
        //先删除specId为specification的Id的所有行
        Example example = new Example(TbSpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("specId",tbSpecification.getId());
        tbSpecificationOptionMapper.deleteByExample(example);
        //在循环遍历插入
        if (optionList != null && optionList.size() != 0) {
            for (TbSpecificationOption option : optionList) {
                option.setSpecId(tbSpecification.getId());
                tbSpecificationOptionMapper.insert(option);
            }
        }
    }

    @Override
    public void delete(Long[] ids) {
        //判断ids是否为空
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                specificationMapper.deleteByPrimaryKey(id);
                Example example = new Example(TbSpecificationOption.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("specId",id);
                tbSpecificationOptionMapper.deleteByExample(example);
            }
        }
    }

}

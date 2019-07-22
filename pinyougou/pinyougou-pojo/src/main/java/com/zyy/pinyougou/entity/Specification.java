package com.zyy.pinyougou.entity;

import com.zyy.pinyougou.pojo.TbSpecification;
import com.zyy.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Zyy
 * @date: 2019-06-21 11:18
 * @description:
 * @version:
 */
public class Specification implements Serializable {

    private TbSpecification tbSpecification;
    private List<TbSpecificationOption> optionList;

    public TbSpecification getTbSpecification() {
        return tbSpecification;
    }

    public void setTbSpecification(TbSpecification tbSpecification) {
        this.tbSpecification = tbSpecification;
    }

    public List<TbSpecificationOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<TbSpecificationOption> optionList) {
        this.optionList = optionList;
    }
}

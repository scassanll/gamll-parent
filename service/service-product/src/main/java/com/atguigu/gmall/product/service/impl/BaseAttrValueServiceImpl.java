package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Scassanl
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service实现
* @createDate 2022-08-23 10:13:50
*/
@Service
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue>
    implements BaseAttrValueService{

    @Resource
    BaseAttrValueMapper baseAttrValueMapper;


    /**
     * 数据修改前根据平台属性ID数据回显的方法实现
     * @param c1Id
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueById(Long c1Id) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("attr_id",c1Id);

        //根据wrapper条件查询属性值
        List<BaseAttrValue> list = baseAttrValueMapper.selectList(wrapper);
        return list;
    }
}





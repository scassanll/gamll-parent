package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BaseCategory1ServiceImpl
        extends ServiceImpl<BaseCategory1Mapper, BaseCategory1>
        implements BaseCategory1Service
{

    @Resource
    BaseCategory2Mapper baseCategory2Mapper;

    /**
     * 查询所有分类并封装成树形菜单结构
     * @return
     */
    @Override
    public List<CategoryTreeTo> getAllCategoryWithTree() {

        List<CategoryTreeTo> list = baseCategory2Mapper.getAllCategoryWithTree();
        return list;
    }
}

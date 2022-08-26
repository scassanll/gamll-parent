package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseCategory1Service extends IService<BaseCategory1> {

    /**
     * 查询所有分类并封装成树形菜单结构
     * @return
     */
    List<CategoryTreeTo> getAllCategoryWithTree();
}

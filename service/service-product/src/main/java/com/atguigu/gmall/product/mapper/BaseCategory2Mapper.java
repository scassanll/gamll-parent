package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Scassanl
* @description 针对表【base_category2(二级分类表)】的数据库操作Mapper
* @createDate 2022-08-22 20:43:14
* @Entity com.atguigu.gmall.product.domain.BaseCategory2
*/
public interface BaseCategory2Mapper extends BaseMapper<BaseCategory2> {

    /**
     * 查询所有分类并封装成树形菜单结构
     * @return
     */
    List<CategoryTreeTo> getAllCategoryWithTree();
}





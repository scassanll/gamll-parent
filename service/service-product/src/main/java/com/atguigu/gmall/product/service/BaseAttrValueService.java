package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Scassanl
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service
* @createDate 2022-08-23 10:13:50
*/
public interface BaseAttrValueService extends IService<BaseAttrValue> {
    /**
     * 数据修改前根据平台属性ID数据回显
     * @param c1Id
     * @return
     */
    List<BaseAttrValue> getAttrValueById(Long c1Id);
}

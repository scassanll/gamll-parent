package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Scassanl
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2022-08-22 23:43:37
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {
    /**
     * 查询某个分类下的所有平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存属性信息
     * @param info
     */
    void saveAttrInfo(BaseAttrInfo info);
}

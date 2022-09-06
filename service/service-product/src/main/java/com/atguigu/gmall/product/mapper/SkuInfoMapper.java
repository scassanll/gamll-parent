package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Scassanl
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-08-23 10:13:50
* @Entity com.atguigu.gmall.product.domain.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 更新SKU的is_sale字段
     * @param skuId
     * @param i
     */
    void changeIsSale(@Param("skuId")Long skuId,@Param("i") int i);

    /**
     * 获取商品实时价格
     * @param skuId
     * @return
     */
    BigDecimal getRealPrice(@Param("skuId") Long skuId);

    /**
     * 查出所有的SkuID
     * @return
     */
    List<Long> getAllSkuId();

}





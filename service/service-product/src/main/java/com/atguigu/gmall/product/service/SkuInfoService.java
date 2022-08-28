package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.math.BigDecimal;
import java.util.List;

/**
* @author Scassanl
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 10:13:50
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 获取商品详情数据
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetail(Long skuId);


    /**
     * 获取商品实时价格
     * @param skuId
     * @return
     */
    BigDecimal get1010Price(Long skuId);

    /**
     * 获取sku_Info信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuDetailSkuInfo(Long skuId);

    /**
     * 查询商品图片信息
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuDetailSkuImage(Long skuId);

}

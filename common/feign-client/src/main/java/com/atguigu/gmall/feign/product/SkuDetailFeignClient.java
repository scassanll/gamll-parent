package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface SkuDetailFeignClient {

//    @GetMapping("/skudetail/{skuId}")
//    Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId);
    /**
     * 查询商品基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);


    /**
     * 查询商品图片信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/image/{skuId}")
    Result<List<SkuImage>> getSkuImage(@PathVariable("skuId") Long skuId);


    /**
     * 获取商品实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}")
    Result<BigDecimal> getSku1010Price(@PathVariable("skuId") Long skuId);

    /**
     * 查询SKU对应的SPU定义的所有销售属性名值
     * 并且标记出当前SKU是哪个
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}/{spuId}")
    Result<List<SpuSaleAttr>> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId,
                                                          @PathVariable("spuId") Long spuId);

    /**
     * 查询SKU组合
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/valuejson/{spuId}")
    Result<String> getSkuValueJson(@PathVariable("spuId") Long spuId);

    /**
     * 根据三级ID查询出整个精确分类路径
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/categoryview/{c3Id}")
    Result<CategoryViewTo> getCategoryView(@PathVariable("c3Id")Long c3Id);
}

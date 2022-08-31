package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情数据库层操作类
 */
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Resource
    BaseCategory3Service baseCategory3Service;

//    @GetMapping("/skudetail/{skuId}")
//    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId")Long skuId){
//        SkuDetailTo skuDetailTo = skuInfoService.getSkuDetail(skuId);
//        return Result.ok(skuDetailTo);}


    /**
     * 查询商品基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId) {

        SkuInfo skuInfo = skuInfoService.getSkuDetailSkuInfo(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 查询商品图片信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/image/{skuId}")
    public Result<List<SkuImage>> getSkuImage(@PathVariable("skuId") Long skuId) {

        List<SkuImage> images = skuInfoService.getSkuDetailSkuImage(skuId);
        return Result.ok(images);
    }

    /**
     * 获取商品实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}")
    public Result<BigDecimal> getSku1010Price(@PathVariable("skuId") Long skuId) {
        {
            BigDecimal price = skuInfoService.get1010Price(skuId);
            return Result.ok(price);
        }
    }

    /**
     * 查询SKU对应的SPU定义的所有销售属性名值
     * 并且标记出当前SKU是哪个
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId,
                                       @PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuId, spuId);
        return Result.ok(saleAttrList);
    }

    /**
     * 查询SKU组合
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/valuejson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId") Long spuId){

        String valueJson = spuSaleAttrService.getAllSaleAttrValueJson(spuId);
        return Result.ok(valueJson);
    }

    /**
     * 根据三级ID查询出整个精确分类路径
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/categoryview/{c3Id}")
    public Result<CategoryViewTo> getCategoryView(@PathVariable("c3Id")Long c3Id){

        CategoryViewTo categoryViewTo = baseCategory3Service.getCategoryView(c3Id);
        return Result.ok(categoryViewTo);
    }
}


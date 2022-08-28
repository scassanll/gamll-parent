package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.item.service.SkuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {


      //可配置的线程池
    @Autowired
    ThreadPoolExecutor executor;


    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    SkuDetailTo detailTo = new SkuDetailTo();
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {

        //开启异步查询商品基本信息，并返回skuInfo
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(()->{
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);

            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);

            return skuInfo;
            },executor);



        //开启异步查询商品图片信息
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            Result<List<SkuImage>> skuImage = skuDetailFeignClient.getSkuImage(skuId);
            skuInfo.setSkuImageList(skuImage.getData());

        },executor);



        //开启异步查询商品实时价格
        CompletableFuture<Void> priceFucture = CompletableFuture.runAsync(() -> {

            Result<BigDecimal> price = skuDetailFeignClient.getSku1010Price(skuId);
            detailTo.setPrice(price.getData());

        }, executor);


        //开启异步查询销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long spuId = skuInfo.getSpuId();

            Result<List<SpuSaleAttr>> skuSaleAttrValues = skuDetailFeignClient.getSkuSaleAttrValues(skuId, skuInfo.getSpuId());
            detailTo.setSpuSaleAttrList(skuSaleAttrValues.getData());

        }, executor);


        //开启异步查询SKU组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(skuInfo.getSpuId());
            detailTo.setValuesSkuJson(skuValueJson.getData());

        }, executor);


        //开启异步查询商品分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            detailTo.setCategoryView(categoryView.getData());
        }, executor);

        //确认每个方法都执行完后放行
        CompletableFuture
                .allOf(imageFuture,priceFucture,saleAttrFuture,skuValueFuture,categoryFuture)
                .join();

        return detailTo;
    }
}

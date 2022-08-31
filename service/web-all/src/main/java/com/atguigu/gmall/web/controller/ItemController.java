package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.web.feign.SkuDetailFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品详情
 */
@Controller
public class ItemController {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    /**
     * 商品详情页
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId,
                       Model model){

        //用Feign调用远程方法返回数据对象
        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);

        //判断调用是否成功
        if (skuDetail.isOk()){
            //若成功则取出Data
            SkuDetailTo skuDetailTo = skuDetail.getData();

            if(skuDetailTo == null || skuDetailTo.getSkuInfo() == null){
                //说明远程没有查到商品
                return "item/404";
            }

            //取出数据放入前端页面
            model.addAttribute("categoryView",skuDetailTo.getCategoryView());
            model.addAttribute("skuInfo",skuDetailTo.getSkuInfo());
            model.addAttribute("price",skuDetailTo.getPrice());
            model.addAttribute("spuSaleAttrList",skuDetailTo.getSpuSaleAttrList());
            model.addAttribute("valuesSkuJson",skuDetailTo.getValuesSkuJson());
        }
        //跳转item首页
        return "item/index";
    }
}

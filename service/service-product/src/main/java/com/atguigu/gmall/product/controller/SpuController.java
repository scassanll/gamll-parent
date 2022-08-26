package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spu功能
 */

@RequestMapping("/admin/product")
@RestController
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    BaseTrademarkService baseTrademarkService;



    /**
     * 商品属性SPU管理的分页查询
     * @param pn 页码
     * @param ps 每页数据条数
     * @param category3Id 三级分类ID
     * @return
     */
    @GetMapping("/{pn}/{ps}")
    public Result getSpuPage(@PathVariable("pn") Long pn,
                             @PathVariable("ps") Long ps,
                             @RequestParam("category3Id") Long category3Id){

        Page<SpuInfo> page = new Page<>(pn,ps);

        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();

        wrapper.eq("category3_id",category3Id);

        Page<SpuInfo> infoPage = spuInfoService.page(page, wrapper);

        return Result.ok(infoPage);

    }


    /**
     * 查询品牌列表
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){

        List<BaseTrademark> list = baseTrademarkService.list();

        return Result.ok(list);
    }

    /**
     * SPU信息保存
     * @param spuInfo
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        spuInfoService.saveSpuInfo(spuInfo);

        return Result.ok();
    }

}

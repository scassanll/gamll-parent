package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("admin/product")
public class SkuController {


    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Resource
    SkuInfoMapper skuInfoMapper;



    /**
     * 获得SKU分页列表
     * @param pn
     * @param ps
     * @return
     */
    @GetMapping("/list/{pn}/{ps}")
    public Result getSkuList(@PathVariable("pn") Long pn,
                             @PathVariable("ps") Long ps){

        Page<SkuInfo> page = new Page<>(pn,ps);
        Page<SkuInfo> result = skuInfoService.page(page);

        return Result.ok(result);
    }

    /**
     * 根据spuId获取图片列表
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId") Long spuId){

        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> list = spuImageService.list(wrapper);

        return Result.ok(list);
    }

    /**
     * 根据spuId获取销售属性
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> spuSaleAttr = spuSaleAttrService.getSaleAttrAndValueBySpuId(spuId);
        return Result.ok(spuSaleAttr);
    }

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }


    /**
     * 上架商品并保存到es中
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId")Long skuId){

        skuInfoService.onSale(skuId,1);

        return Result.ok();
    }

    /**
     * 下架商品并从es中删除
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId")Long skuId){

        skuInfoService.cancelSale(skuId,0);

        return Result.ok();
    }
}

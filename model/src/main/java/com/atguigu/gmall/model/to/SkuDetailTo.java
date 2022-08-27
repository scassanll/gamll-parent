package com.atguigu.gmall.model.to;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuDetailTo {

    private CategoryViewTo categoryView;    //商品分类信息

    private SkuInfo skuInfo;         //商品基本信息

    private BigDecimal price;       //实时价格

    private List<SpuSaleAttr> spuSaleAttrList; //SPU的所有销售属性列表

    private Object valuesSkuJson;   //
}

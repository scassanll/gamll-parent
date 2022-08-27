package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
* @author Scassanl
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-23 10:13:50
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Resource
    SkuInfoMapper skuInfoMapper;
    @Resource
    SkuImageService imageService;
    @Resource
    SkuAttrValueService skuAttrValueService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    SkuImageService skuImageService;

    @Resource
    SpuSaleAttrService spuSaleAttrServices;


    /**
     * 后台SKU数据保存
     * @param skuInfo
     */
    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        // sku基本信息保存到 sku_info
        save(skuInfo);
        Long infoId = skuInfo.getId();

        // sku图片信息保存到 sku_image
        for (SkuImage skuImage : skuInfo.getSkuImageList()) {
            skuImage.setSkuId(infoId);
        }
        imageService.saveBatch(skuInfo.getSkuImageList());

        // sku的平台属性名和值的关系保存到 sku_attr_value
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(infoId);
        }
        skuAttrValueService.saveBatch(skuAttrValueList);


        // sku的销售属性名和值的关系保存到 sku_sale_attr_value
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(infoId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
    }

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //查询skuInfo
        Long category3Id = skuInfo.getCategory3Id();

        //商品SKU的基本信息
        detailTo.setSkuInfo(skuInfo);

        //商品SKU所属的完整分类信息
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getBaseCategoryView(category3Id);
        detailTo.setCategoryView(categoryViewTo);

        //商品SKU的图片
        List<SkuImage> imageList = skuImageService.getImageList(skuId);
        skuInfo.setSkuImageList(imageList);

        //商品实时价格
        BigDecimal price = get1010Price(skuId);
        detailTo.setPrice(price);

        //商品销售属性
        List<SpuSaleAttr> saleAttrList = spuSaleAttrServices.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        detailTo.setSpuSaleAttrList(saleAttrList);

        return detailTo;
    }

    /**
     * 获取商品实时价格
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal get1010Price(Long skuId) {

        BigDecimal price = skuInfoMapper.getRealPrice(skuId);
        return price;
    }


}





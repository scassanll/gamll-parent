package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Scassanl
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2022-08-23 10:13:50
*/
@Transactional //开启事务
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{

    @Resource
    SpuInfoMapper spuInfoMapper;
    @Resource
    SpuImageService spuImageService;
    @Resource
    SpuSaleAttrService spuSaleAttrService;
    @Resource
    SpuSaleAttrValueService spuSaleAttrValueService;

    /**
     * SPU的属性大保存
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        //保存属性名
        spuInfoMapper.insert(spuInfo);

        //获取属性ID
        Long infoId = spuInfo.getId();

        //遍历图片对象并回填属性ID
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(infoId);
        }
        //批量保存图片
        spuImageService.saveBatch(spuImageList);

        //销售属性对象并回填属性ID
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(infoId);

            //遍历销售属性值并回填销售属性名和SpuId
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(infoId);
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            }
            //批量保存销售属性值
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList);
        }
        //批量保存销售属性名
        spuSaleAttrService.saveBatch(spuSaleAttrList);



    }
}





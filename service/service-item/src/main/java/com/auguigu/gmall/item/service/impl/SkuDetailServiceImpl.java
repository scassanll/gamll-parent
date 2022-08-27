package com.auguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.auguigu.gmall.item.feign.SkuDetailFeignClient;
import com.auguigu.gmall.item.service.SkuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {

        SkuDetailTo skuDetailTo = new SkuDetailTo();

        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);

        return skuDetail.getData();
    }
}

package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BloomDataQueryServiceImpl implements BloomDataQueryService {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 布隆数据查询服务的方法实现
     * @return
     */
    @Override
    public List queryDate() {
        return skuInfoService.findAllSkuId();
    }
}

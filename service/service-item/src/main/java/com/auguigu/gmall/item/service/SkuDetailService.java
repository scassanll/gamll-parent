package com.auguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.stereotype.Service;


public interface SkuDetailService {
    SkuDetailTo getSkuDetail(Long skuId);
}

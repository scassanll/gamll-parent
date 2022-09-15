package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

public interface SkuDetailService {
    SkuDetailTo getSkuDetail(Long skuId);

    /**
     * 更新商品热度分
     * @param skuId
     */
    void updateHostScore(@Param("skuId") Long skuId);
}

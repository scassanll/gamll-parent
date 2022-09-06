package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class BloomOpsServiceImpl implements BloomOpsService{

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoService skuInfoService;



    /**
     * 重建指定布隆过滤器
     * @param bloomName
     * @param dataQueryService
     */
    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService dataQueryService) {

        //获得老布隆过滤器
        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);
        //提前准备一个新的布隆过滤器，后面替换使用
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("newBloom");

        //拿到所有商品的ID
        List list = dataQueryService.queryDate();

        //初始化新布隆
        bloomFilter.tryInit(5000000,0.00001);

        //将商品ID放入新布隆里
        for (Object skuId : list) {
            bloomFilter.add(skuId);
        }

        //新老布隆交换
        oldBloomFilter.rename("oldBloomFilter");    //老布隆下线
        bloomFilter.rename(bloomName);                 //新布隆上线

        //6、删除老布隆，和中间交换层
        oldBloomFilter.deleteAsync();
        redissonClient.getBloomFilter("oldBloomFilter").deleteAsync();
    }
}

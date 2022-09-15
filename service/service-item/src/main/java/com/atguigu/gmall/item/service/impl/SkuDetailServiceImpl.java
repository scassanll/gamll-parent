package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.service.CacheOpsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service  //单实例service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuProductFeignClient skuProductFeignClient;


    /**
     * Map作为缓存【本地缓存】：优缺点
     * 优点：
     * 缺点：
     *   1、100w的数据内存够不够
     */

    /**
     * 可配置的线程池，可自动注入
     */
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SearchFeignClient searchFeignClient;


    //每个skuId，关联一把自己的锁
    Map<Long,ReentrantLock> lockPool = new ConcurrentHashMap<>();
    //锁的粒度太大了，把无关的人都锁住了
    ReentrantLock lock = new ReentrantLock(); //锁的住


    @Autowired
    CacheOpsService cacheOpsService;


    /**
     * 表达式中的params代表方法的所有参数列表
     * @param skuId
     * @return
     */
    @GmallCache(
            cacheKey =SysRedisConst.SKU_INFO_PREFIX+"#{#params[0]}",
            bloomName = SysRedisConst.BLOOM_SKUID,
            bloomValue = "#{#params[0]}",
            lockName = SysRedisConst.LOCK_SKU_DETAIL+"#{#params[0]}"
    )

    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();

        //开启异步查询商品基本信息，并返回skuInfo
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(()->{
            Result<SkuInfo> result = skuProductFeignClient.getSkuInfo(skuId);

            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);

            return skuInfo;
        },executor);



        //开启异步查询商品图片信息
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            if (skuInfo != null) {
                Result<List<SkuImage>> skuImage = skuProductFeignClient.getSkuImage(skuId);
                skuInfo.setSkuImageList(skuImage.getData());
            }
        },executor);



        //开启异步查询商品实时价格
        CompletableFuture<Void> priceFucture = CompletableFuture.runAsync(() -> {

            Result<BigDecimal> price = skuProductFeignClient.getSku1010Price(skuId);
            detailTo.setPrice(price.getData());

        }, executor);


        //开启异步查询销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Long spuId = skuInfo.getSpuId();
                Result<List<SpuSaleAttr>> skuSaleAttrValues = skuProductFeignClient.getSkuSaleAttrValues(skuId, spuId);
                detailTo.setSpuSaleAttrList(skuSaleAttrValues.getData());

            }
        }, executor);


        //开启异步查询SKU组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            if (skuInfo != null) {
                Result<String> skuValueJson = skuProductFeignClient.getSkuValueJson(skuInfo.getSpuId());
                detailTo.setValuesSkuJson(skuValueJson.getData());

            }
        }, executor);


        //开启异步查询商品分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            if (skuInfo != null) {
                Result<CategoryViewTo> categoryView = skuProductFeignClient.getCategoryView(skuInfo.getCategory3Id());
                detailTo.setCategoryView(categoryView.getData());
            }
        }, executor);

        //确认每个方法都执行完后放行
        CompletableFuture
                .allOf(imageFuture,priceFucture,saleAttrFuture,skuValueFuture,categoryFuture)
                .join();

        return detailTo;
    }


    /**
     *
     * 切面点表达式怎么写？
     *  execution(* com.atguigu.gmall.item.**.*(..))
     * @param skuId
     * @return
     */
    public SkuDetailTo getSkuDetailWithCache(Long skuId) {
        String cacheKey = SysRedisConst.SKU_INFO_PREFIX +skuId;
        //1、先查缓存
        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
        //2、判断
        if(cacheData == null){
            //3、缓存没有
            //4、先问布隆，是否有这个商品
            boolean contain = cacheOpsService.bloomContains(skuId);
            if(!contain){
                //5、布隆说没有，一定没有
                log.info("[{}]商品 - 布隆判定没有，检测到隐藏的攻击风险....",skuId);
                return null;
            }
            //6、布隆说有，有可能有，就需要回源查数据
            boolean lock = cacheOpsService.tryLock(skuId); //为当前商品加自己的分布式锁。100w的49号查询只会放进一个
            if(lock){
                //7、获取锁成功，查询远程
                log.info("[{}]商品 缓存未命中，布隆说有，准备回源.....",skuId);
                SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
                //8、数据放缓存
                cacheOpsService.saveData(cacheKey,fromRpc);
                //9、解锁
                cacheOpsService.unlock(skuId);
                return fromRpc;
            }
            //9、没获取到锁
            try {Thread.sleep(1000);
               return cacheOpsService.getCacheData(cacheKey,SkuDetailTo.class);
            } catch (InterruptedException e) {

            }
        }
        //4、缓存中有
        return cacheData;
    }

      //500w  100w：49  100w：50  100w：51   100w: 52    100w: 53
    public SkuDetailTo getSkuDetailXxxxFeature(Long skuId) {
        lockPool.put(skuId,new ReentrantLock());
        //每个不同的sku，用自己专用的锁
        //1、看缓存中有没有  sku:info:50
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if ("x".equals(jsonStr)) {
            //说明以前查过，只不过数据库没有此记录，为了避免再次回源，缓存了一个占位符
            return null;
        }
        //
        if (StringUtils.isEmpty(jsonStr)) {
            //2、redis没有缓存数据
            //2.1、回源。之前可以判断redis中保存的sku的id集合，有没有这个id
            //防止随机值穿透攻击？ 回源之前，先要用布隆/bitmap判断有没有
//            int result = getbit(49);

            SkuDetailTo fromRpc = null;


//            ReentrantLock lock = new ReentrantLock();  //锁不住

//            lock.lock(); //等锁，必须等到锁
            //判断锁池中是否有自己的锁
            //锁池中不存在就放一把新的锁，作为自己的锁，存在就用之前的锁
            ReentrantLock lock = lockPool.putIfAbsent(skuId, new ReentrantLock());

            boolean b = this.lock.tryLock(); //立即尝试加锁，不用等，瞬发。等待逻辑在业务上 .抢一下，不成就不用再抢了
//            boolean b = lock.tryLock(1, TimeUnit.SECONDS); //等待逻辑在锁上.1s内，CPU疯狂抢锁
            if(b){
                //抢到锁
                fromRpc = getSkuDetailFromRpc(skuId);
            }else {
                //没抢到
//                Thread.sleep(1000);
                jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
                //逆转为 SkuDetailTo
                return null;
            }

            //2.2、放入缓存【查到的对象转为json字符串保存到redis】
            String cacheJson = "x";
            if (fromRpc != null) {
                cacheJson = Jsons.toStr(fromRpc);
                //加入雪崩解决方案。固定业务时间+随机过期时间
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 7, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 30, TimeUnit.MINUTES);
            }

            return fromRpc;
        }
        //3、缓存中有. 把json转成指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
        return skuDetailTo;
    }

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
        return fromRpc;
    }


    /**
     * 更新商品热度分
     * @param skuId
     */
    @Override
    public void updateHostScore(@Param("skuId") Long skuId) {

        Long increment = redisTemplate.opsForValue()
                .increment(SysRedisConst.SKU_HOTSCORE_PREFIX + skuId);
        if (increment % 100 == 0){
            searchFeignClient.updateHotScore(skuId,increment);
        }
    }
}

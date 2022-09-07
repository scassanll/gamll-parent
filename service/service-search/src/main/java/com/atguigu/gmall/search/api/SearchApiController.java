package com.atguigu.gmall.search.api;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsSrvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inner/rpc/search")
@RestController
public class SearchApiController {

    @Autowired
    GoodsSrvice goodsService;

    /**
     * 保存商品到es中
     * @param goods
     * @return
     */
    @PostMapping("/goods")
    public Result saveGoods(@RequestBody Goods goods){

        goodsService.saveGoods(goods);

        return Result.ok();
    }

    /**
     * 将ES中的商品信息删除
     * @param skuId
     * @return
     */
    @DeleteMapping("/goods/{skuId}")
    public Result deleteGoods(@PathVariable("skuId") Long skuId){

        goodsService.deleteGoods(skuId);

        return Result.ok();
    }

    /**
     * 商品检索
     * @param paramVo
     * @return
     */
    @PostMapping("/goods/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo){

        SearchResponseVo responseVo = goodsService.search(paramVo);
        return Result.ok(responseVo);
    }

    @GetMapping("/goods/hotscore/{skuId}")
    public Result increHotScore(@PathVariable("skuId") Long skuId,
                                @RequestParam("score") Long score){

        goodsService.updateHotScore(skuId,score);

        return Result.ok();
    }
}

package com.atguigu.gmall.search.api;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.search.service.GoodsSrvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/inner/rpc/search")
@RestController
public class SearchApiController {

    @Autowired
    GoodsSrvice goodsSrvice;

    /**
     * 保存商品到es中
     * @param goods
     * @return
     */
    @PostMapping("/goods")
    public Result saveGoods(@RequestBody Goods goods){

        goodsSrvice.saveGoods(goods);

        return Result.ok();
    }
}

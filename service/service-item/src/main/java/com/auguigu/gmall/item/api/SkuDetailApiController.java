package com.auguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.auguigu.gmall.item.service.SkuDetailService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "三级分类的RPC接口")
@RestController
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailApiController {

    @Autowired
    SkuDetailService detailService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId){



        SkuDetailTo skuDetailTo = detailService.getSkuDetail(skuId);


        return Result.ok(skuDetailTo);
    }

}

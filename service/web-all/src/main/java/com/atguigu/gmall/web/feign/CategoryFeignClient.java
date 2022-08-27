package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("service-product")
public interface CategoryFeignClient {

    /**
     * 给service-product服务发送远程请求
     * 请求路径/api/inner/rpc/product/category/tree
     * @return
     */
    @GetMapping("/api/inner/rpc/product/category/tree")
    Result<List<CategoryTreeTo>> getCategorytree();
}

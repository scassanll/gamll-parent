package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类有关的API
 * 远程调用都是内部接口
 * 命名规范：/api/inner/rpc/模块名/路径
 */
@Api(tags = "三级分类RPC接口")
@RestController
@RequestMapping("/api/inner/rpc/product")
public class CategoryApiController {

    @Autowired
    BaseCategory1Service baseCategory1Service;
    /**
     * 查询所有分类并封装成树形菜单结构
     * @return
     */
    @ApiOperation("三级分类树形结构查询")
    @GetMapping("/category/tree")
    public Result getAllCategoryWithTree(){

        List<CategoryTreeTo> list = baseCategory1Service.getAllCategoryWithTree();

        return Result.ok(list);
    }
}

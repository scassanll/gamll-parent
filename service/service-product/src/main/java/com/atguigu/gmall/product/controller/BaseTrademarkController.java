package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/admin/product")
@RestController
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 品牌列表分页查询
     * @param pg 页码
     * @param size 每页长度
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{size}")
    public Result baseTrademark(@PathVariable("page") Long pg,
                                @PathVariable("size") Long size){

        Page<BaseTrademark> page = new Page<>(pg,size);
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page);

        return Result.ok(pageResult);
    }

    /**
     * 根据品牌ID查询信息
     * @param id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result baseTrademark(@PathVariable("id") Long id){

        BaseTrademark result = baseTrademarkService.getById(id);
        return Result.ok(result);
    }

    /**
     * 根据ID修改品牌信息
     * @param baseTrademark
     * @return
     */
    @PutMapping("/baseTrademark/update")
    public Result baseTrademarkUpdate(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /**
     * 新增品牌信息
     * @param trademark
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark trademark){

        baseTrademarkService.save(trademark);
        return Result.ok();
    }

    /**
     * 根据id删除品牌信息
     * @param id
     * @return
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable("id") Long id){

        baseTrademarkService.removeById(id);
        return Result.ok();
    }
}

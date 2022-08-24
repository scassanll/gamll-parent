package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 查询某个分类下的所有平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id")Long category1Id,
                                @PathVariable("category2Id")Long category2Id,
                                @PathVariable("category3Id")Long category3Id)
    {
        List<BaseAttrInfo>  list = baseAttrInfoService.getAttrInfoAndValueByCategoryId(category1Id,category2Id,category3Id);

        return Result.ok(list);
    }


    /**
     * 保存属性信息
     * @return
     */

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo info){

        baseAttrInfoService.saveAttrInfo(info);

        return Result.ok();
    }

    /**
     * 修改前根据平台属性ID数据回显
     * @param c1Id
     * @return
     */
    @GetMapping("/getAttrValueList/{c1Id}")
    public Result getAttrValueList(@PathVariable("c1Id") Long c1Id){

        List<BaseAttrValue> list = baseAttrValueService.getAttrValueById(c1Id);

        return Result.ok(list);
    }
}

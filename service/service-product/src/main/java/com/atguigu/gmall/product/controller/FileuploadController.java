package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin/product")
@RestController
public class FileuploadController {


    @PostMapping("/fileUpload")
    public Result fileUpload(){

        return Result.ok();
    }

}

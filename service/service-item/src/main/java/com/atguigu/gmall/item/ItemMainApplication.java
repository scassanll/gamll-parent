package com.atguigu.gmall.item;


import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;



/**
 * 处理商品详情类
 */
@EnableThreadPool
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product",
                                    "com.atguigu.gmall.feign.search"})
@SpringCloudApplication
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}

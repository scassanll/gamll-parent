package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableDiscoveryClient
//@EnableCircuitBreaker
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign"
})
@SpringBootApplication
public class WebAllMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class,args);
    }
}

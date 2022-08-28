package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;



//@EnableDiscoveryClient 开启熔断降级
//@EnableCircuitBreaker  开启服务发现
//@SpringBootApplication //主启动类
@SpringCloudApplication
public class GatewayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class,args);
    }
}

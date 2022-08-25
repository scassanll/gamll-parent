package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置文件
 */
@EnableTransactionManagement //开启基于注解的事务
@Configuration  //告诉springboot这是一个配置类
public class MyBaitsPlusConfig {

    //把MybatisPlus的插件主体（总插件）放到spring容器里
    @Bean
    public MybatisPlusInterceptor interceptor(){

        //插件主体
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //加入内部的小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();

        //页码溢出后默认访问最后一页
        paginationInnerInterceptor.setOverflow(true);

        //分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }

}

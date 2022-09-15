package com.atguigu.gmall.web.config;


import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class WebAllConfiguration {

    //添加openfeign的拦截器，给feign的请求设置userId进入到头信息
    @Bean
    public RequestInterceptor userHeaderInterceptor() {

        return (requestTemplate -> {
            //通过spring的监听器获取到 原生的servlet请求属性，获取到请求，获取到头信息
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String userId = request.getHeader(SysRedisConst.USERID_HEADER);
            //透传用户id
            requestTemplate.header(SysRedisConst.USERID_HEADER, userId);
            //透传临时id
            String tempId = request.getHeader(SysRedisConst.USERTEMPID_HEADER);
            requestTemplate.header(SysRedisConst.USERTEMPID_HEADER, tempId);
        });
    }
}


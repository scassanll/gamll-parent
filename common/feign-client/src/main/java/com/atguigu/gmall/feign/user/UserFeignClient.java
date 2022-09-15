package com.atguigu.gmall.feign.user;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/inner/rpc/user")
@FeignClient("service-user")
public interface UserFeignClient {

    /**
     * 获得用户所有收货地址
     * @return
     */
    @GetMapping("/address/list")
    Result<List<UserAddress>> getUserAddressList();

}

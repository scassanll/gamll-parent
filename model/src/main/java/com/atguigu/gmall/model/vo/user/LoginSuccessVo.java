package com.atguigu.gmall.model.vo.user;

import lombok.Data;

@Data
public class LoginSuccessVo {

    //用户令牌
    private String token;
    //用户
    private String nickName;

}

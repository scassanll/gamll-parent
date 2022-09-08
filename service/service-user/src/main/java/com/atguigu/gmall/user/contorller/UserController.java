package com.atguigu.gmall.user.contorller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    UserInfoService userInfoService;

    /**
     * 用户登录
     * @return
     */
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo info){

        String loginName = info.getLoginName();
        String passwd = info.getPasswd();

        //查询用户
        LoginSuccessVo vo = userInfoService.login(loginName,passwd);

        //登录成功
        if (vo!=null) {
            return Result.ok(vo);
        }
        //登陆失败
        return Result.build("",ResultCodeEnum.LOGIN_ERROR);
    }

    /**
     * 用户退出
     * @param token
     * @return
     */
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){

        userInfoService.logout(token);
        return Result.ok();
    }

}

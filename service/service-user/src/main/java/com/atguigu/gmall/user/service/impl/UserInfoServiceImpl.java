package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Scassanl
 * @description 针对表【user_info(用户表)】的数据库操作Service实现
 * @createDate 2022-09-08 01:19:48
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 用户登录
     *
     * @param loginName
     * @param passwd
     * @return
     */
    @Override
    public LoginSuccessVo login(String loginName, String passwd) {


        LoginSuccessVo vo = new LoginSuccessVo();

        //查询数据库判断用户是否存在
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        String md5Passwd = MD5.encrypt(passwd);
        wrapper.eq(UserInfo::getLoginName, loginName).eq(UserInfo::getPasswd, md5Passwd);
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        //登陆成功
        if (userInfo != null) {

            //生成令牌
            String token = UUID.randomUUID().toString().replace("-", "");
            //绑定令牌
            redisTemplate.opsForValue().set(SysRedisConst.LOGIN_USER + token, Jsons.toStr(userInfo), 7, TimeUnit.DAYS);

            vo.setToken(token);
            vo.setNickName(userInfo.getNickName());
            return vo;
        }
        return null;


    }

    /**
     * 用户退出
     * @param token
     */
    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}





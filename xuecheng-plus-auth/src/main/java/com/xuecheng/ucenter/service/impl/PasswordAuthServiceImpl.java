package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Description: 密码验证服务
 * @Author: Lishebly
 * @Date: 2024/3/23/24/4:12 PM
 * @Version: 1.0
 */
@Service("password_authservice")
@Slf4j
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CheckCodeClient checkCodeClient;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser == null){
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }
        String checkcode = authParamsDto.getCheckcode();
        if (StringUtils.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");
        }
        //校验验证码
        Boolean verify = checkCodeClient.verify(authParamsDto.getCheckcodekey(), checkcode);
        if (!verify){
            //返回空表示验证码错误
            throw new RuntimeException("验证码错误");
        }
        //校验密码
        String passwordSrc = authParamsDto.getPassword();
        String passwordDB = xcUser.getPassword();
        boolean matches = passwordEncoder.matches(passwordSrc, passwordDB);
        if (!matches){
            //返回空表示密码错误
            throw new RuntimeException("账号或者密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;

    }
}

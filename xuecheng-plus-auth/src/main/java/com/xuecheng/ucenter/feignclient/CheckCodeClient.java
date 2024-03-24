package com.xuecheng.ucenter.feignclient;

import com.xuecheng.ucenter.feignclient.fallback.CheckCodeClientFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: 验证码服务接口
 * @Author: Lishebly
 * @Date: 2024/3/23/24/4:57 PM
 * @Version: 1.0
 */
@FeignClient(value = "checkcode",fallbackFactory = CheckCodeClientFactory.class)
@RequestMapping("/checkcode")
public interface CheckCodeClient {

    @PostMapping(value = "/verify")
    public Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);

}

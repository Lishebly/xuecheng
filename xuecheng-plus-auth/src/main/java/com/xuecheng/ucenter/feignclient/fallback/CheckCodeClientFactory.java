package com.xuecheng.ucenter.feignclient.fallback;

import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/23/24/5:02 PM
 * @Version: 1.0
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                System.out.println("调用验证码服务熔断异常：" + throwable.getMessage());
                return null;
            }
        };
    }
}

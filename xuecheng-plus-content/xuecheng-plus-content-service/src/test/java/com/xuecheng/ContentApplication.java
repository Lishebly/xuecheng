package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/3/24/10:23 AM
 * @Version: 1.0
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.xuecheng.content.feignclient"})
//@EnableCircuitBreaker
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}

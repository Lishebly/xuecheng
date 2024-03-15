package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/2/24/4:43 PM
 * @Version: 1.0
 */
@EnableSwagger2Doc
@SpringBootApplication
//nacos配置
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}

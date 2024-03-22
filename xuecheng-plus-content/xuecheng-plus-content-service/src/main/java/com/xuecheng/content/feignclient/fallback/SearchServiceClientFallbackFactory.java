package com.xuecheng.content.feignclient.fallback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.model.dto.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/20/24/8:37 PM
 * @Version: 1.0
 */
@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用搜索发生熔断走降级方法,熔断异常:", throwable.getMessage());
                return false;
            }
        };
    }
}

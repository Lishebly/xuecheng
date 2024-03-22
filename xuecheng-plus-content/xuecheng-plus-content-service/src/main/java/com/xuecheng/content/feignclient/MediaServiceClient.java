package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.fallback.MediaServiceClientFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description: 媒资管理服务客户端
 * @Author: Lishebly
 * @Date: 2024/3/19/24/5:08 PM
 * @Version: 1.0
 */
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class,fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {


    @PostMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(
            @RequestPart("filedata") MultipartFile upload,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "objectName", required = false) String objectName
    );



}



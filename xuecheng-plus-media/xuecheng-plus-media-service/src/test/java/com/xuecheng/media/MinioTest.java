package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/8/24/8:49 AM
 * @Version: 1.0
 */

public class MinioTest {
    private static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //生成一个上传文件方法
    @Test
    public void uploadFile() {
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".png");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        try {
            UploadObjectArgs args = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("001/1.png")
                    .filename("/Users/lilijiabao/Downloads/玫瑰花变成小鸟.png")
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(args);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //读取文件
    @Test
    public void read() {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket("testbucket")
                    .object("001/1.png")
                    .build();
            FilterInputStream inputStream = minioClient.getObject(args);
            FileOutputStream fileOutputStream = new FileOutputStream(new File("/Users/lilijiabao/Downloads/1.png"));
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除文件
    @Test
    public void delete() {
        try {
            RemoveObjectArgs testbucket = RemoveObjectArgs.builder()
                    .bucket("testbucket")
                    .object("001/1.png")
                    .build();
            minioClient.removeObject(testbucket);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getObjName() {
        //根据年月日生成目录格式为2024/1/1/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
        System.out.println(simpleDateFormat.format(new Date()));
    }
}

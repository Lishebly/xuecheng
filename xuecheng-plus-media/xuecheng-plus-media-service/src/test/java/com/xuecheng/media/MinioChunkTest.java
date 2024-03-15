package com.xuecheng.media;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @Description: minio分块上传测试
 * @Author: Lishebly
 * @Date: 2024/3/8/24/2:00 PM
 * @Version: 1.0
 */
public class MinioChunkTest {

    private static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void testUpload() {
        File sourceFile = new File("/Users/lilijiabao/Downloads/ceshi/bigfile_test /Day6-05.上传视频-合并分块.mp4");

        //把文件分块
        //分块大小
        long chunkSize = 1024 * 1024 * 10;
        //源文件大小
        long sourceFileSize = sourceFile.length();
        long chunkNum = (long) Math.ceil(sourceFileSize * 1.0 / chunkSize);
        for (long i = 0; i < chunkNum; i++) {

        }
    }
}

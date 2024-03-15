package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 断点上传测试
 * @Author: Lishebly
 * @Date: 2024/3/8/24/11:15 AM
 * @Version: 1.0
 */
public class BigFileTest {

    @Test
    public void testBigFile() throws Exception {
        File sourceFile = new File("/Users/lilijiabao/Downloads/ceshi/bigfile_test /Day6-05.上传视频-合并分块.mp4");
        File destFile = new File("/Users/lilijiabao/Downloads/ceshi/bigfile_test /chunk");
        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        //分块大小
        long chunkSize = 1024 * 1024 * 10;
        //源文件大小
        long sourceFileSize = sourceFile.length();
        long chunkNum = (long) Math.ceil(sourceFileSize * 1.0 / chunkSize);
        //设置缓冲区
        byte[] buffer = new byte[1024];
        //读取源文件
        RandomAccessFile r = new RandomAccessFile(sourceFile, "r");
        for (long i = 0; i < chunkNum; i++) {
            //创建分块文件
            File chunkFile = new File(destFile, String.valueOf(i));
            //写
            if (chunkFile.exists()) {
                chunkFile.delete();
            }
            boolean newFile = chunkFile.createNewFile();
            if (newFile){
                RandomAccessFile rw = new RandomAccessFile(chunkFile, "rw");
                int len;
                while ((len = r.read(buffer)) != -1) {
                    rw.write(buffer, 0, len);
                    if (chunkFile.length() == chunkSize){
                        break;
                    }
                }
                rw.close();
                System.out.println("分块文件"+i+"写入完成");
            }

        }
        r.close();

    }


    @Test
    public void readAccounts() throws Exception {
        File sourceFile = new File("/Users/lilijiabao/Downloads/ceshi/bigfile_test /Day6-05.上传视频-合并分块.mp4");
        File mergedFile = new File("/Users/lilijiabao/Downloads/ceshi/bigfile_test /new.mp4");
        File destFile = new File("/Users/lilijiabao/Downloads/ceshi/bigfile_test /chunk");
        if (mergedFile.exists()) {
            mergedFile.delete();
        }
        //创建新的合并文件
        mergedFile.createNewFile();
        RandomAccessFile rw = new RandomAccessFile(mergedFile, "rw");
        List<File> collect = Arrays.stream(destFile.listFiles()).sorted((o1, o2) -> Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName())).collect(Collectors.toList());
        //缓冲区
        byte[] buffer = new byte[1024];
        for (File file : collect) {
            RandomAccessFile r = new RandomAccessFile(file, "r");
            int len;
            while ((len = r.read(buffer)) != -1) {
                rw.write(buffer, 0, len);
            }
            r.close();
        }
        rw.close();

        //比较md5
        try (

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                FileInputStream mergeFileStream = new FileInputStream(mergedFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }

    }
}

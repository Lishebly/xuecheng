package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/11/24/2:54 PM
 * @Version: 1.0
 */
@Component
@Slf4j
public class VideoTask {


    @Autowired
    MediaFileService mediaFileService;


    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;


    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        //获取当前分片号,分片总数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //获取系统线程数
        int processors = Runtime.getRuntime().availableProcessors();
        //根据分片号,分片总数查询任务表
        List<MediaProcess> mediaProcessList = mediaFileService.getMediaProcessList(shardIndex, shardTotal, processors);
        //查询后开启多线程执行任务
        int size = mediaProcessList.size();
        log.debug("取出了{}条任务",size);
        if (size <= 0){
            return;
        }
        //执行任务,开启 size 个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //开启计数器
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(() -> {
                //执行任务
                boolean task = mediaFileService.startTask(mediaProcess.getId());
                //看看是否抢占的到,如果抢占到,则执行任务
                if(! task){
                    log.debug("没有抢占到任务,任务id:{}",mediaProcess.getId());
                    return;
                }
                log.debug("抢占到任务,开始执行任务,任务id:{}",mediaProcess.getId());
                //获取视频,将视频下载为服务器临时文件
                File downloadFileFromMinIO = mediaFileService.downloadFileFromMinIO(mediaProcess.getBucket(), mediaProcess.getFilePath());
                if(downloadFileFromMinIO == null){
                    log.debug("下载视频失败,任务id:{}",mediaProcess.getId());
                    mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), null, "从 minio下载视频失败");
                    return;
                }
                //处理结束的视频文件
                File mp4File = null;
                try {
                    mp4File = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    log.error("创建mp4临时文件失败");
                    mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), null, "创建mp4临时文件失败");
                    return;
                }
                String result = "";
                //调用 ffmpeg 对视频进行转码
                try {
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegpath, downloadFileFromMinIO.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
                    result = mp4VideoUtil.generateMp4();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                    mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), null, "ffmpeg处理视频文件出错");
                    return;
                }
                if (!result.equals("success")){
                    log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), result);
                    mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), null, "ffmpeg处理视频文件出错");
                    return;
                }
                //成功之后的处理
                //拼接 minio 路径
                String minioPath = getFilePath(mediaProcess.getFileId(),".mp4");
                //将文件上传至 minio,把 minio 之前的文件删掉
                try {
                    boolean uploadToMinio = mediaFileService.uploadToMinio(mp4File.getAbsolutePath(), mediaProcess.getBucket(), mediaFileService.getMimeType(".mp4"), minioPath);
                    if (!uploadToMinio){
                        log.error("上传视频文件到 minio失败,任务id:{}",mediaProcess.getId());
                        mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), null, "上传视频文件到 minio 失败");
                        return;
                    }
                    String url = "/" + mediaProcess.getBucket() + "/" + minioPath;
                    //更新状态
                    mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "2", mediaProcess.getFileId(), url, null);
                    log.debug("任务id:{}处理完成",mediaProcess.getId());
                    //删除 minio 之前文件
                    boolean b = mediaFileService.deleteFileInMinio(mediaProcess.getBucket(), mediaProcess.getFilePath());
                    if (b){
                        log.debug("删除minio之前文件成功,任务id:{}",mediaProcess.getId());
                    }
                } catch (Exception e) {
                    log.error("上传视频文件到 minio失败,任务id:{}",mediaProcess.getId());
                    mediaFileService.saveProcessFinishStatus(mediaProcess.getId(), "3", mediaProcess.getFileId(), null, "上传视频文件到 minio 失败");
                    return;
                } finally {
                    countDownLatch.countDown();
                }
            });

        });
        //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }
    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}

package com.xuecheng.content.service.jobhandler;

import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/17/24/3:59 PM
 * @Version: 1.0
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        int courseId =  Integer.parseInt(businessKey1);
        //课程静态化
        generateCourseHtml(mqMessage,courseId);
        //课程索引,es
        saveCourseIndex(mqMessage,courseId);
        //课程缓存
        saveCourseCache(mqMessage,courseId);


        return true;

    }
    /**
     * 生成课程静态化页面,上传到文件系统
     * @param mqMessage
     * @param courseId
     */
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        log.debug("开始进行课程静态化,课程 Id 为:{}", courseId);
        //消息 Id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        int stageOne = mqMessageService.getStageOne(id);
        //已经完成,不用再处理
        if (stageOne > 0) {
            log.debug("课程静态化完成直接返回,课程 Id 为:{}", courseId);
            return;
        }

        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            log.debug("课程静态化失败,课程 Id 为:{}", courseId);
            return;
        }
        coursePublishService.uploadCourseHtml(courseId,file);
        //保存第一阶段状态
        mqMessageService.completedStageOne(id);
        log.debug("课程静态化完成,课程 Id 为:{}", courseId);
    }

    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){

        //if (true){
        //    throw new RuntimeException("缓存课程信息失败");
        //}
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        log.debug("保存课程索引信息,课程id:{}",courseId);
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        //已经完成,不用再处理
        if (stageTwo > 0) {
            log.debug("课程静态化完成直接返回,课程 Id 为:{}", courseId);
            return;
        }
        Boolean result = coursePublishService.saveCourseIndex(courseId);
        if(result){
            //保存第一阶段状态
            mqMessageService.completedStageTwo(id);
        }

    }

}

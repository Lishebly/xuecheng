package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CourseIndex;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/15/24/9:18 PM
 * @Version: 1.0
 */
@Service
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;
    @Autowired
    private CoursePublishMapper coursePublishMapper;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseTeacherService courseTeacherService;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private CoursePublishService coursePublishService;
    @Autowired
    private MediaServiceClient mediaServiceClient;
    @Autowired
    private SearchServiceClient searchServiceClient;

    /**
     * 根据课程id，查询课程预览信息
     *
     * @param courseId
     * @return
     */
    @Override
    public CoursePreviewDto findCoursePreview(Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.findById(courseId);
        List<TeachplanDto> teachplanTreeNodes = teachplanService.findTeachplanTreeNodes(courseId);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanTreeNodes);
        return coursePreviewDto;
    }

    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //获取审核状态
        String auditStatus = courseBase.getAuditStatus();
        //判断审核状态是否为未审核,如果是则需要改成已提交
        if ("202003".equals(auditStatus)){
            XueChengPlusException.cast("当前审核状态已提交,请等审核完成后再进行重新提交");
        }
        //判断公司是否一样
        if (!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("当前课程与提交的公司不一致");
        }
        //判断课程是否有图片
        if (courseBase.getPic() == null || courseBase.getPic().isEmpty()){
            XueChengPlusException.cast("当前课程没有图片");
        }
        //向课程预发布表插入信息
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //获取课程基本信息加营销信息
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.findById(courseId);
        BeanUtils.copyProperties(courseBaseInfoDto, coursePublishPre);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String jsonString = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(jsonString);
        //课程信息
        List<TeachplanDto> teachplanTreeNodes = teachplanService.findTeachplanTreeNodes(courseId);
        if (teachplanTreeNodes == null || teachplanTreeNodes.isEmpty()){
            XueChengPlusException.cast("当前课程没有课程计划");
        }
        String jsonString1 = JSON.toJSONString(teachplanTreeNodes);
        coursePublishPre.setTeachplan(jsonString1);
        //师资信息
        List<CourseTeacher> courseTeachers = courseTeacherService.findCourseTeacher(courseId);
        if (courseTeachers == null || courseTeachers.isEmpty()){
            XueChengPlusException.cast("当前课程没有师资信息");
        }
        String jsonString2 = JSON.toJSONString(courseTeachers);
        coursePublishPre.setTeachers(jsonString2);
        coursePublishPre.setStatus("202003");
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre1 != null){
            coursePublishPreMapper.updateById(coursePublishPre);
        }else {
            coursePublishPreMapper.insert(coursePublishPre);
        }
        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 发布课程
     *
     * @param companyId
     * @param courseId
     */
    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
        //约束校验

        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null){
            XueChengPlusException.cast("当前课程不存在,请提交审核后再提交");
        }
        if (!coursePublishPre.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("不允许提交其它公司的课程");
        }
        if (!"202004".equals(coursePublishPre.getStatus())){
            XueChengPlusException.cast("审核未通过不可发布");
        }
        //保存课程信息
        saveCousePublish(coursePublishPre);
        //保存消息表
        saveMessage(coursePublishPre.getId());
        //删除课程预发布表信息
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * 生成课程html
     *
     * @param courseId
     * @return
     */
    @Override
    public File generateCourseHtml(Long courseId) {

        File tmpFile = null;
        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = coursePublishService.findCoursePreview(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //输出流
            tmpFile = File.createTempFile("course", ".html");
            FileOutputStream outputStream = new FileOutputStream(tmpFile);
            IOUtils.copy(inputStream, outputStream);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("生成课程html失败",e);
            XueChengPlusException.cast("生成课程html失败");
        }
        return tmpFile;

    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String s = mediaServiceClient.uploadFile(multipartFile, "course", String.valueOf(courseId));
        if (s == null){
            log.debug("课程静态化发生熔断");
            XueChengPlusException.cast("上传文件发生异常");
        }
    }

    @Override
    public Boolean saveCourseIndex(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add){
            log.error("添加课程索引失败");
            XueChengPlusException.cast("添加课程索引失败");
        }
        return add;
    }

    private void saveMessage(Long courseId) {
        MqMessage coursePublish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (coursePublish == null){
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }

    private void saveCousePublish(CoursePublishPre coursePublishPre) {
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublish1 = coursePublishMapper.selectById(coursePublishPre.getId());
        if (coursePublish1 != null){
            coursePublishMapper.updateById(coursePublish);
        }else {
            coursePublishMapper.insert(coursePublish);
        }
        //更新课程基本表的审核状态
        CourseBase courseBase = courseBaseMapper.selectById(coursePublishPre.getId());
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }
}

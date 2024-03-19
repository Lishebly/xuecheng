package com.xuecheng.content.controller;

import org.springframework.web.bind.annotation.*;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 课程发布 前端控制器
 * </p>
 *
 * @author itcast
 */
@Slf4j
@RestController
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
public class CoursePublishController {

    @Autowired
    private CoursePublishService  coursePublishService;




}

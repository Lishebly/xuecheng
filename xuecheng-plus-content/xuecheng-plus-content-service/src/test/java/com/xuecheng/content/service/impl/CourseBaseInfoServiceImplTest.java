package com.xuecheng.content.service.impl;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseBaseInfoServiceImplTest {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Test
    public void testFindCourseList() {
        System.out.println("testFindCourseList");
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        //queryCourseParamsDto.setPublishStatus("203001");
        queryCourseParamsDto.setAuditStatus("202004");
        PageParams pageParams = new PageParams(1L, 10L);
        PageResult<CourseBase> courseList = courseBaseInfoService.findCourseList(pageParams, queryCourseParamsDto);
        System.out.println(courseList);
    }

}
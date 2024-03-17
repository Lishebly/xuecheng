package com.xuecheng.content.service.impl;

import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private TeachplanService teachplanService;

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
}

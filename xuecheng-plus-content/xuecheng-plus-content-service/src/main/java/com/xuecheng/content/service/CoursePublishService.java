package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * @Description: 课程发布服务
 * @Author: Lishebly
 * @Date: 2024/3/15/24/9:16 PM
 * @Version: 1.0
 */
public interface CoursePublishService {

    /**
     * 获取课程发布所需信息
     * @param courseId 课程id
     * @return 返回结果
     */
    public CoursePreviewDto findCoursePreview(Long courseId);
}

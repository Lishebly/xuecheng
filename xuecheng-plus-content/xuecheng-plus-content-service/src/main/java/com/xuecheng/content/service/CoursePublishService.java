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

    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
    public void commitAudit(Long companyId,Long courseId);

    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/20 16:23
     */
    public void publish(Long companyId,Long courseId);

}

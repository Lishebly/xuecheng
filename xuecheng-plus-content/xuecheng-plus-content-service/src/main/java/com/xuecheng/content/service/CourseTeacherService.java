package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddTeacherDto;
import com.xuecheng.content.model.dto.EditTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/5:33 PM
 * @Version: 1.0
 */
public interface CourseTeacherService {
    /**
     * 根据课程id查询课程教师
     * @param courseId
     * @return
     */
    List<CourseTeacher> findCourseTeacher(Long courseId);

    /**
     * 添加课程教师
     * @param editTeacherDto
     * @return
     */
    CourseTeacher addCourseTeacher(EditTeacherDto editTeacherDto);

    /**
     * 修改课程教师
     * @param editTeacherDto
     * @return
     */
    CourseTeacher updateCourseTeacher(EditTeacherDto editTeacherDto);

    /**
     * 删除课程教师
     * @param id
     * @param courseId
     */
    void deleteCourseTeacher(Long id, Long courseId);
}

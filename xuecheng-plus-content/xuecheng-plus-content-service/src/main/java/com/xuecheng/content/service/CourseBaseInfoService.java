package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @Description: 课程信息管理接口
 * @Author: Lishebly
 * @Date: 2024/3/3/24/11:09 AM
 * @Version: 1.0
 */
public interface CourseBaseInfoService {
    /**
     * 课程分页查询
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 查询条件
     * @return 课程分页结果
     */
    public PageResult<CourseBase> findCourseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 增加课程
     * @param addCourseDto
     * @return
     */
    CourseBaseInfoDto addCourse(Long companyId,AddCourseDto addCourseDto);

    /**
     * 根据id查询课程信息
     * @param id
     * @return
     */
    CourseBaseInfoDto findById(Long id);


    /**
     * 修改课程信息
     * @param companyId
     * @param editCourseDto
     * @return
     */
    CourseBaseInfoDto update(Long companyId,EditCourseDto editCourseDto);

    /**
     * 删除课程
     * @param id
     */
    void delete(Long id);
}

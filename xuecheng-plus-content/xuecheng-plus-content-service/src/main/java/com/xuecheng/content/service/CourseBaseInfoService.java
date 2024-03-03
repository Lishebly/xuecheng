package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
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
}

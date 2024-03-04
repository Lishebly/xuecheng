package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/4/24/1:15 PM
 * @Version: 1.0
 */
public interface CourseCategoryService {

    /**
     * 课程分类树查询接口
     * @return 课程分类树
     */
    public List<CourseCategoryTreeDto> getCourseCategories(String id);
}

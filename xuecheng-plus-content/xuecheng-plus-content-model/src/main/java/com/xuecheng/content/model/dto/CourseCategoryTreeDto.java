package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 课程分类树型结点dto
 * @Author: Lishebly
 * @Date: 2024/3/4/24/1:06 PM
 * @Version: 1.0
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    private List<CourseCategoryTreeDto> childrenTreeNodes;
}

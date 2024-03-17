package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/15/24/9:10 PM
 * @Version: 1.0
 */
@Data
@ToString
public class CoursePreviewDto {

    //课程基本信息
    //课程营销信息
    private CourseBaseInfoDto courseBase;
    //课程计划信息
    private List<TeachplanDto> teachplans;
    //课程师资信息
    //private TeacherDto teacherDto;
}

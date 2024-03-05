package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/10:06 PM
 * @Version: 1.0
 */
@Data
@ApiModel(value = "AddTeacherDto", description = "添加课程教师信息")
public class AddTeacherDto {
    /**
     * 课程标识
     */
    @NotEmpty(message = "课程标识不能为空")
    @ApiModelProperty(value = "课程标识", required = true)
    private Long courseId;

    /**
     * 教师姓名
     */
    @NotEmpty(message = "教师姓名不能为空")
    @ApiModelProperty(value = "教师姓名", required = true)
    private String teacherName;

    /**
     * 教师职位
     */
    @NotEmpty(message = "教师职位不能为空")
    @ApiModelProperty(value = "教师职位", required = true)
    private String position;

    /**
     * 教师简介
     */
    @ApiModelProperty(value = "教师简介")
    private String introduction;
}

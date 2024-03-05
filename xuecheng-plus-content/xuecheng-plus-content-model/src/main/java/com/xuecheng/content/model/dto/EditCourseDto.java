package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/9:51 AM
 * @Version: 1.0
 */
@Data
@ApiModel(value="EditCourseDto", description="编辑课程基本信息")
public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}

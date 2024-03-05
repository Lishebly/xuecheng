package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/10:13 PM
 * @Version: 1.0
 */
@Data
@ApiModel(value="EditTeacherDto", description="编辑教师信息")
public class EditTeacherDto extends AddTeacherDto{
//    "id": 24,
//
//"photograph": null,
//"createDate": null
    @ApiModelProperty(value = "教师id", required = true)
    private Long id;
    @ApiModelProperty(value = "教师照片")
    private String photograph;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;
}

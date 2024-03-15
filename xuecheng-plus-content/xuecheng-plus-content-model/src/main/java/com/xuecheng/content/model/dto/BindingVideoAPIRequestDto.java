package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/13/24/7:30 PM
 * @Version: 1.0
 */
@Data
@ApiModel(value = "绑定视频API请求参数", description = "绑定视频API请求参数")
public class BindingVideoAPIRequestDto {
    @ApiModelProperty(value = "媒体id", required = true)
    private String mediaId;
    @ApiModelProperty(value = "文件名", required = true)
    private String fileName;
    @ApiModelProperty(value = "教学计划id", required = true)
    private Long teachplanId;
}

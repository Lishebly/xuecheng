package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @Description: 查询课程列表参数
 * @Author: Lishebly
 * @Date: 2024/3/2/24/3:58 PM
 * @Version: 1.0
 */
@Data
@ToString
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}

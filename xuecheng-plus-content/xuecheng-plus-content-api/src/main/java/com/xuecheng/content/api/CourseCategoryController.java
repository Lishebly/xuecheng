package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/4/24/1:14 PM
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(value = "课程分类管理", tags = "课程分类管理")
public class CourseCategoryController {
    @Autowired
    private CourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    @ApiOperation("课程分类树查询接口")
    public List<CourseCategoryTreeDto> list() {
        log.info("课程分类树查询接口");
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.getCourseCategories("1");
        return courseCategoryTreeDtos;
    }
}

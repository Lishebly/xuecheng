package com.xuecheng.content.service.impl;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseCategoryServiceImplTest {

    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    public void testGetCourseCategories() {
        System.out.println("testGetCourseCategories");
        List<CourseCategoryTreeDto> courseCategories = courseCategoryService.getCourseCategories("1");
        System.out.println(courseCategories);
    }



}
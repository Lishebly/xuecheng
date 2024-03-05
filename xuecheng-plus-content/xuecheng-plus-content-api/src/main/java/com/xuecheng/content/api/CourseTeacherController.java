package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.AddTeacherDto;
import com.xuecheng.content.model.dto.EditTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/5:32 PM
 * @Version: 1.0
 */

@RestController
@Api(value = "课程教师管理", tags = "课程教师管理接口")
@Slf4j
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;

    //根据课程id查询课程教师
    @ApiOperation("根据课程id查询课程教师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> findCourseTeacher(@PathVariable("courseId") Long courseId) {
        log.info("根据课程id查询课程教师");
        return courseTeacherService.findCourseTeacher(courseId);
    }

    //添加课程教师
    @ApiOperation("添加课程教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher addCourseTeacher(@RequestBody EditTeacherDto editTeacherDto) {
        log.info("添加课程教师");
        return courseTeacherService.addCourseTeacher(editTeacherDto);
    }

    //修改课程教师
    @ApiOperation("修改课程教师")
    @PutMapping("/courseTeacher")
    public CourseTeacher updateCourseTeacher(@RequestBody EditTeacherDto editTeacherDto) {
        log.info("修改课程教师");
        return courseTeacherService.updateCourseTeacher(editTeacherDto);
    }

    //删除课程教师
    @ApiOperation("删除课程教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable("id") Long id, @PathVariable("courseId") Long courseId) {
        log.info("删除课程教师");
        courseTeacherService.deleteCourseTeacher(id,courseId);
    }

}

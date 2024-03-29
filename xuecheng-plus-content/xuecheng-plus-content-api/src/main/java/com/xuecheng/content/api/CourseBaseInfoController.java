package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/2/24/4:37 PM
 * @Version: 1.0
 */
@RestController
@Api(value = "课程基本信息管理", tags = "课程基本信息管理")
@Slf4j
public class CourseBaseInfoController {
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @PostMapping("/course/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @ApiOperation("课程查询接口")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        log.info("课程查询接口");
        return courseBaseInfoService.findCourseList(pageParams, queryCourseParamsDto);

    }

    @PostMapping("/course")
    @ApiOperation("课程新增接口")
    public CourseBaseInfoDto add(@RequestBody @Validated(ValidationGroups.AddCourse.class) AddCourseDto addCourseDto) {
        log.info("课程新增接口");
        Long companyId = 1L;
        return courseBaseInfoService.addCourse(companyId,addCourseDto);
    }

    //根据课程 id，查询课程基本信息
    @GetMapping("/course/{id}")
    @ApiOperation("根据课程 id，查询课程基本信息")
    public CourseBaseInfoDto findById(@PathVariable("id") @Validated(ValidationGroups.UpdateCourse.class) Long id) {
        log.info("根据课程 id，查询课程基本信息");
        //Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //log.info("principal:{}",principal);
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        log.info("user:{}",user);
        return courseBaseInfoService.findById(id);
    }

    //根据课程 id，修改课程基本信息
    @PutMapping("/course")
    @ApiOperation("根据课程 id，修改课程基本信息")
    public CourseBaseInfoDto update(@RequestBody @Validated(ValidationGroups.UpdateCourse.class) EditCourseDto editCourse) {
        log.info("根据课程 id，修改课程基本信息");
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.update(companyId,editCourse);
    }

    //删除课程
    @DeleteMapping("/course/{id}")
    @ApiOperation("删除课程")
    public void delete(@PathVariable("id") Long id) {
        log.info("删除课程");
        courseBaseInfoService.delete(id);
    }

}

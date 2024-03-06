package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.AddTeacherDto;
import com.xuecheng.content.model.dto.EditTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/5:34 PM
 * @Version: 1.0
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;


    /**
     * 根据课程id查询课程教师
     *
     * @param courseId
     * @return
     */
    @Override
    public List<CourseTeacher> findCourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<CourseTeacher>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher addCourseTeacher(EditTeacherDto editTeacherDto) {
        Long id = editTeacherDto.getId();
        if (id == null){
            CourseTeacher courseTeacher = new CourseTeacher();
            BeanUtils.copyProperties(editTeacherDto, courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            courseTeacherMapper.insert(courseTeacher);
            return getCourseTeacherById(courseTeacher.getId());
        }else {
            return updateCourseTeacher(editTeacherDto);
        }
    }

    @Override
    public CourseTeacher updateCourseTeacher(EditTeacherDto editTeacherDto) {
        CourseTeacher courseTeacher = courseTeacherMapper.selectById(editTeacherDto.getId());
        BeanUtils.copyProperties(editTeacherDto, courseTeacher);
        courseTeacherMapper.updateById(courseTeacher);
        return getCourseTeacherById(courseTeacher.getId());
    }

    /**
     * 根据id删除课程教师
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public void deleteCourseTeacher(Long id, Long courseId) {
        //把课程信息中的教师信息删除
        //删除课程教师信息
        if (id != null){
            courseTeacherMapper.deleteById(id);
        }else {
            courseTeacherMapper.delete(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));
        }

    }

    /**
     * 根据id查询课程教师
     *
     * @param id
     * @return
     */
    private CourseTeacher getCourseTeacherById(Long id) {
        return courseTeacherMapper.selectById(id);
    }
}

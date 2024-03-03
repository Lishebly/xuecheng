package com.xuecheng.content;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/3/24/10:29 AM
 * @Version: 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseBaseMapperTests {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper() {
        System.out.println("testCourseBaseMapper");
        CourseBase courseBase = courseBaseMapper.selectById(18L);
        //详细进行分页查询的单元测试
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setPublishStatus("203001");
        queryCourseParamsDto.setAuditStatus("202004");
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据课程名称进行模糊查询
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //根据课程状态进行精确查询
        queryWrapper.eq(queryCourseParamsDto.getAuditStatus() != null, CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //根据课程发布进行精确查询
        queryWrapper.eq(queryCourseParamsDto.getPublishStatus() != null, CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        //创建分页对象 参数1：当前页 参数2：每页显示条数
        Page<CourseBase> page = new Page<>(1, 10);
        //执行分页查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        //获取查询结果
        List<CourseBase> records = courseBasePage.getRecords();
        //获取总记录数
        long total = courseBasePage.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, 1, 10);
        Assertions.assertNotNull(courseBase);
    }
}

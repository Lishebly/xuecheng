package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/3/24/11:13 AM
 * @Version: 1.0
 */

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 课程分页查询
     *
     * @param pageParams           分页参数
     * @param queryCourseParamsDto 查询条件
     * @return 课程分页结果
     */
    @Override
    public PageResult<CourseBase> findCourseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        log.info("课程分页查询");
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据课程名称进行模糊查询
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //根据课程状态进行精确查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //根据课程发布进行精确查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        //创建分页对象 参数1：当前页 参数2：每页显示条数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //执行分页查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        //获取查询结果
        List<CourseBase> records = courseBasePage.getRecords();
        //获取总记录数
        long total = courseBasePage.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, 1, 10);
        return courseBasePageResult;
    }

    /**
     * 增加课程
     * @param
     * @return
     */
    @Override
    @Transactional
    public CourseBaseInfoDto addCourse(Long companyId,AddCourseDto dto) {
        CourseBase courseBaseNew = getCourseBase(companyId, dto);
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(courseBaseNew.getId());
        int i = saveCourseMarket(courseMarket);
        if (i < 0){
            throw new RuntimeException("课程营销信息保存失败");
        }
        return getCourseBaseInfoDto(courseBaseNew.getId());
    }

    /**
     * 根据课程id查询课程基本信息
     * @param id
     * @return
     */
    @Override
    public CourseBaseInfoDto findById(Long id) {
        return getCourseBaseInfoDto(id);
    }

    /**
     * 修改课程
     * @param
     * @return
     */
    @Override
    public CourseBaseInfoDto update(Long companyId,EditCourseDto editCourseDto) {
        Long id = editCourseDto.getId();
        //判断公司id
        CourseBase courseBase = courseBaseMapper.selectById(id);
        //判断是否为空
        if (courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }
        if (!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        courseBase = new CourseBase();
        BeanUtils.copyProperties(editCourseDto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        //todo 修改人
        courseBase.setChangePeople("1");
        courseBaseMapper.updateById(courseBase);
        //修改课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        saveCourseMarket(courseMarket);
        return getCourseBaseInfoDto(editCourseDto.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CourseBase courseBase = courseBaseMapper.selectById(id);
        String auditStatus = courseBase.getAuditStatus();
        if ("202002".equals(auditStatus)){
            courseBaseMapper.deleteById(id);
            courseMarketMapper.deleteById(id);
            teachplanService.deleteTeachplanByCourseId(id);
            courseTeacherService.deleteCourseTeacher(null,id);
        }else {
            XueChengPlusException.cast("课程已发布，不能删除");
        }

    }

    /**
     * 根据课程id查询课程基本信息
     * @param id
     * @return
     */
    private CourseBaseInfoDto getCourseBaseInfoDto(Long id) {
        //查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(id);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        //查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //查询分类信息
        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        courseBaseInfoDto.setMtName(courseCategory.getName());
        //查询小分类信息
        courseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setStName(courseCategory.getName());
        return courseBaseInfoDto;
    }

    /**
     * 保存课程营销信息
     * @param courseMarket
     * @return
     */
    private int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            XueChengPlusException.cast("请选择课程收费规则");
        }
        if (charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice().floatValue()<=0){
                XueChengPlusException.cast("课程价格不能为空且必须大于0");
            }
        }
        CourseMarket courseMarket1 = courseMarketMapper.selectById(courseMarket.getId());
        if (courseMarket1 == null){
            return courseMarketMapper.insert(courseMarket);
        }else {
            BeanUtils.copyProperties(courseMarket,courseMarket1);
            courseMarket1.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarket1);
        }
    }

    /**
     * 获取课程基本信息
     * @param companyId
     * @param dto
     * @return
     */
    private  CourseBase getCourseBase(Long companyId, AddCourseDto dto) {
        /*//合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            XueChengPlusException.cast("课程名称不能为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            XueChengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            XueChengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            XueChengPlusException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            XueChengPlusException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            XueChengPlusException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            XueChengPlusException.cast("收费规则为空");
        }*/
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        return courseBaseNew;
    }
}

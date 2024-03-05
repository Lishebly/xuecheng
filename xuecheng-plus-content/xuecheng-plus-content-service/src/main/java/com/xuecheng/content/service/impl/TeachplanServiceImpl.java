package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/12:51 PM
 * @Version: 1.0
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;

    /**
     * 查询课程计划树形结构
     *
     * @param courseId
     * @return
     */
    @Override
    public List<TeachplanDto> findTeachplanTreeNodes(Long courseId) {
        List<TeachplanDto> list = teachplanMapper.findTeachplanTreeNodes(courseId);
        return list;
    }

    /**
     * 保存或修改课程计划
     *
     * @param saveTeachplanDto
     */
    @Override
    public void saveOrUpdateTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();
        if (id == null) {
            //设置一个函数获取父亲节点的儿子总数
            int count = findTeachplanCount(saveTeachplanDto.getCourseId(),
                    saveTeachplanDto.getParentid());
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplan.setCreateDate(LocalDateTime.now());
            teachplan.setOrderby(count+1);
            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplan.setChangeDate(LocalDateTime.now());
            teachplanMapper.updateById(teachplan);
        }

    }

    /**
     * 查询课程计划总数
     *
     * @param courseId
     * @param parentid
     * @return
     */
    private int findTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId).eq(Teachplan::getParentid,parentid);
        return teachplanMapper.selectCount(queryWrapper);
    }
}

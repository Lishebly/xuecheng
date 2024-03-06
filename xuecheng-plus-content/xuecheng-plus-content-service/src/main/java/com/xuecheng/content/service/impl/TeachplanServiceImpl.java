package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.exception.DeleteException;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

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
     * 删除课程计划
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteTeachplan(Long id) {
        //查找父节点 id 为 id 的节点,如果有那么提示不可删除
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,id);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (!teachplans.isEmpty()) {
            //提示不可删除
            DeleteException.cast("课程计划信息还有子级信息，无法操作",120409L);
        }
        //如果是小章节则需要把关联的数据一并删除
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer grade = teachplan.getGrade();
        if (grade == 2){
            //删除关联的媒体数据
            LambdaQueryWrapper<TeachplanMedia> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(TeachplanMedia::getTeachplanId,id);
            teachplanMediaMapper.delete(queryWrapper1);
        }
        teachplanMapper.deleteById(id);
    }

    /**
     * 移动接口
     * @param type
     * @param courseId
     */
    @Override
    public void move(String type, Long courseId) {
        if (type.equals("movedown")){
            //下移
            Teachplan teachplan = teachplanMapper.selectById(courseId);
            Teachplan next = teachplanMapper.getNextOrder(teachplan.getParentid(),teachplan.getOrderby());
            if (next == null){
                //已经是最后一个了，提示不可移动
                return;
            }
            swapOrder(teachplan,next);
        }else if (type.equals("moveup")){
            //上移
            Teachplan teachplan = teachplanMapper.selectById(courseId);
            Teachplan prev = teachplanMapper.getPrevOrder(teachplan.getParentid(),teachplan.getOrderby());
            if (prev == null){
                //已经是第一个了，提示不可移动
                return;
            }
            swapOrder(teachplan,prev);
        }


        //本质上是获取两个课程 id 互换它俩排序字段
    }

    /**
     * 根据课程id删除课程计划
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteTeachplanByCourseId(Long id) {
        //删除关联的媒体数据
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getCourseId,id);
        teachplanMediaMapper.delete(queryWrapper);
        //删除课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Teachplan::getCourseId,id);
        teachplanMapper.delete(queryWrapper1);
    }

    private void swapOrder(Teachplan t1, Teachplan t2) {
        Integer orderby = t1.getOrderby();
        t1.setOrderby(t2.getOrderby());
        t2.setOrderby(orderby);
        teachplanMapper.updateById(t1);
        teachplanMapper.updateById(t2);
    }

    /**
     * 查询课程计划总数
     *
     * @param courseId
     * @param parentid
     * @return
     */
    private int findTeachplanCount(Long courseId, Long parentid) {
        /*LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId).eq(Teachplan::getParentid,parentid);
        return teachplanMapper.selectCount(queryWrapper);*/
        Map map = new HashMap<>();
        map.put("courseId",courseId);
        map.put("parentId",parentid);
        return teachplanMapper.getMaxOrderby(map) == null ? 0 : teachplanMapper.getMaxOrderby(map);

    }
}

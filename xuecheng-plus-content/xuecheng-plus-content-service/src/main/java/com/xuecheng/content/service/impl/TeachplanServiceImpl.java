package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

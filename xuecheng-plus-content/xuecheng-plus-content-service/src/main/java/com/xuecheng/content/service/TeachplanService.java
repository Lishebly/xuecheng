package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/12:50 PM
 * @Version: 1.0
 */
public interface TeachplanService {
    /**
     * 查询课程计划树形结构
     * @param courseId
     * @return
     */
    List<TeachplanDto> findTeachplanTreeNodes(Long courseId);
}

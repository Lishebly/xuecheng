package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
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

    /**
     * 保存或更新课程计划
     * @param saveTeachplanDto
     */
    void saveOrUpdateTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param id
     */
    void deleteTeachplan(Long id);

    /**
     * 移动接口
     * @param type
     * @param courseId
     */
    void move(String type, Long courseId);
}

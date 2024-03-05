package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * 根据课程id，查询课程计划树形结构
     * @param courseId
     * @return
     */
    List<TeachplanDto> findTeachplanTreeNodes(Long courseId);

    /**
     *
     * @param parentId
     * @param orderby
     * @return
     */
    Teachplan getNextOrder(@Param("parentId") Long parentId, @Param("orderby") Integer orderby);


    /**
     *
     * @param parentId
     * @param orderby
     * @return
     */
    Teachplan getPrevOrder(@Param("parentId") Long parentId, @Param("orderby") Integer orderby);

    Integer getMaxOrderby(Map map);
}

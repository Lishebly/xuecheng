package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/4/24/1:15 PM
 * @Version: 1.0
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 课程分类树查询接口
     *
     * @return 课程分类树
     */
    @Override
    public List<CourseCategoryTreeDto> getCourseCategories(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //将list转为 map,同时过滤根节点
        Map<String, CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).collect(Collectors.toMap(CourseCategory::getId, key -> key));
        //创建返回对象
        List<CourseCategoryTreeDto> categoryTreeDtosVo = new ArrayList<>();
        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{
            if (id.equals(item.getParentid())){
                categoryTreeDtosVo.add(item);
            }else {
                //获得当前节点父亲节点
                CourseCategoryTreeDto father = map.get(item.getParentid());
                //查看 father 节点子节点列表有没有
                if (father.getChildrenTreeNodes() == null){
                    father.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                father.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtosVo;
    }
}

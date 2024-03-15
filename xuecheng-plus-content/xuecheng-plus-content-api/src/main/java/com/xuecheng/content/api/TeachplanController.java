package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindingVideoAPIRequestDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/11:10 AM
 * @Version: 1.0
 */
@RestController
//课程计划管理
@Api(value = "课程计划管理", tags = "课程计划管理接口")
@Slf4j
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    //查询课程计划树形结构
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> findTeachplanTreeNodes(@PathVariable("courseId") Long courseId) {
      log.info("查询课程计划树形结构");
      return teachplanService.findTeachplanTreeNodes(courseId);
    }

    //新增,修改接口
    @ApiOperation("新增,修改接口")
    @PostMapping("/teachplan")
    public void findTeachplanTreeNodes(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        log.info("新增,修改接口");
        teachplanService.saveOrUpdateTeachplan(saveTeachplanDto);
    }

    //删除课程计划的接口
    @ApiOperation("删除课程计划的接口")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable("id") Long id) {
        log.info("删除课程计划的接口");
        teachplanService.deleteTeachplan(id);
    }


    //移动接口
    @ApiOperation("移动接口")
    @PostMapping("/teachplan/{type}/{courseId}")
    public void moveDown(@PathVariable String type,@PathVariable Long courseId) {
        log.info("移动接口");
        teachplanService.move(type,courseId);
    }


    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindingVideoAPIRequestDto bindingVideoAPIRequestDto){
        log.info("课程计划和媒资信息绑定");
        teachplanService.bindVideo(bindingVideoAPIRequestDto);
    }

    @ApiOperation(value = "根据课程id删除课程计划和媒资信息绑定")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deleteAssociationMedia(@PathVariable("teachPlanId") Long teachPlanId,@PathVariable("mediaId") String mediaId){
        log.info("根据课程id删除课程计划和媒资信息绑定");
        teachplanService.deleteAssociationMedia(teachPlanId,mediaId);
    }
}

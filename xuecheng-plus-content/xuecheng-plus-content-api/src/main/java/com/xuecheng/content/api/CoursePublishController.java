package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/15/24/10:19 AM
 * @Version: 1.0
 */
@Controller
@Slf4j
public class CoursePublishController {

    @Autowired
    private CoursePublishService coursePublishService;
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView coursePreview(@PathVariable("courseId") Long courseId){
        log.info("coursePreview");
        ModelAndView modelAndView = new ModelAndView();
        CoursePreviewDto coursePreview = coursePublishService.findCoursePreview(courseId);
        modelAndView.addObject("model",coursePreview);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }



}

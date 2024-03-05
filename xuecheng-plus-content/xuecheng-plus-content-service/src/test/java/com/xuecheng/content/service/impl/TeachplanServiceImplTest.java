

package com.xuecheng.content.service.impl;

import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TeachplanServiceImplTest {
    @Autowired
    TeachplanService telplanService;

    @Test
    public void testFindTeachplanTreeNodes() {
        System.out.println("testFindTeachplanTreeNodes");
        List<TeachplanDto> teachplanTreeNodes = telplanService.findTeachplanTreeNodes(117L);
        System.out.println(teachplanTreeNodes);
    }

}
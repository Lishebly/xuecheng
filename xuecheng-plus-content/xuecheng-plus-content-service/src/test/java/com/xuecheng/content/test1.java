package com.xuecheng.content;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/21/24/12:40 PM
 * @Version: 1.0
 */

public class test1 {

    @Test
    public void test1() {
        System.out.println("test1");
        HashMap<Integer, Integer> map = new HashMap<>();
        map.values().forEach(a -> System.out.println(a));
        map.containsKey(0);
    }
}


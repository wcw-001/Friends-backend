package com.wcw.usercenter.service;

import com.wcw.usercenter.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 */
@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    void test(){
        String str1 = "fancydog是狗";
        String str2 = "fancydog不是狗";
        String str3 = "fancydog是人不是狗";
        int score1 = AlgorithmUtils.minDistance(str1,str2);
        int score2 = AlgorithmUtils.minDistance(str1,str3);
        System.out.println(score1);
        System.out.println(score2);
    }
    @Test
    void testCompareTags(){
        List<String> tagList1 = Arrays.asList("java", "大一", "男");
        List<String> tagList2 = Arrays.asList("java", "大一", "女");
        List<String> tagList3 = Arrays.asList("java", "大二", "女");
        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println(score1);
        System.out.println(score2);

    }
}

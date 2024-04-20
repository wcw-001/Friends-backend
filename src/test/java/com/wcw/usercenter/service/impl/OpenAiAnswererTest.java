package com.wcw.usercenter.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAiAnswererTest {

    @Test
    void doAnswer01() {
        OpenAiAnswerer test = new OpenAiAnswerer();
        test.doAnswer("你好");
    }
}

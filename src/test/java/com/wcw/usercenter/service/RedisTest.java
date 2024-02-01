package com.wcw.usercenter.service;

import com.wcw.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
//@RunWith(SpringRunner.class)
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("wcwString","dog");
        valueOperations.set("wcwInt",1);
        valueOperations.set("wcwDouble",2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("wcw");
        valueOperations.set("wcwUser",user);
        Object yupi = valueOperations.get("wcwString");
        Assertions.assertTrue("dog".equals((String) yupi));
        yupi = valueOperations.get("wcwInt");
        Assertions.assertTrue(1==(Integer) yupi);
        yupi = valueOperations.get("wcwDouble");
        Assertions.assertTrue(2.0 == (Double) yupi);
        System.out.println(valueOperations.get("wcwUser"));

    }

}

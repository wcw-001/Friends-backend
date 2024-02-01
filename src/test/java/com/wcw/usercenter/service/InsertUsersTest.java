package com.wcw.usercenter.service;

import com.wcw.usercenter.mapper.UserMapper;
import com.wcw.usercenter.model.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InsertUsersTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000; //52秒
        List<User> userList = new ArrayList<>();
        for(int i = 0 ;i < INSERT_NUM; i++){
            User user = new User();
            user.setUsername("假喜欢");
            user.setUserAccount("Fancywcw");
            user.setAvatarUrl("http://s6he8mzr3.hn-bkt.clouddn.com/wallpaper1705021285121.jpg");
            user.setGender(0);
            user.setProfile("");
            user.setUserPassword("12345678");
            user.setPhone("2314123");
            user.setEmail("123456@qq.com");
            user.setUserStatus(0);
            user.setIsDelete(0);
            user.setUserRole(0);
            user.setUserCode("111111");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000; //15秒
        //分十组
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for(int i = 0;i<10;i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假喜欢");
                user.setUserAccount("Fancywcw");
                user.setAvatarUrl("http://s6he8mzr3.hn-bkt.clouddn.com/wallpaper1705021285121.jpg");
                user.setGender(0);
                user.setProfile("");
                user.setUserPassword("12345678");
                user.setPhone("2314123");
                user.setEmail("123456@qq.com");
                user.setUserStatus(0);
                user.setIsDelete(0);
                user.setUserRole(0);
                user.setUserCode("111111");
                user.setTags("[]");
                userList.add(user);
                if (j % 10000 == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, 10000);
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}

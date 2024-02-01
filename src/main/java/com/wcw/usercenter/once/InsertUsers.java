package com.wcw.usercenter.once;

import com.wcw.usercenter.mapper.UserMapper;
import com.wcw.usercenter.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
    public void doInsertUsers(){
        final int INSERT_NUM = 10000000;
        for(int i = 0 ;i < INSERT_NUM; i++){
            StopWatch stopWatch = new StopWatch();
            stopWatch.stop();
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
            userMapper.insert(user);
        }

    }
}

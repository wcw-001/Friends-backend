package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.Follow;
import com.wcw.usercenter.model.vo.UserVo;

import java.util.List;

/**
* @author Shier
* @description 针对表【follow】的数据库操作Service
* @createDate 2023-06-11 13:02:31
*/
public interface FollowService extends IService<Follow> {

    void followUser(Long followUserId, Long userId);

    List<UserVo> listFans(Long userId);

    List<UserVo> listMyFollow(Long userId);
}

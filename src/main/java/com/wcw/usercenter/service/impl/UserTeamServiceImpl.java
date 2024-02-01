package com.wcw.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wcw.usercenter.mapper.UserTeamMapper;
import com.wcw.usercenter.model.domain.UserTeam;
import com.wcw.usercenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author wcw
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-01-27 18:35:51
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}





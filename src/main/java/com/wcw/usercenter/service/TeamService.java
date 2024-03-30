package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.Team;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.dto.TeamQuery;
import com.wcw.usercenter.model.request.TeamJoinRequest;
import com.wcw.usercenter.model.request.TeamQuitRequest;
import com.wcw.usercenter.model.request.TeamUpdateRequest;
import com.wcw.usercenter.model.vo.TeamUserVo;

import java.util.List;


/**
* @author wcw
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-01-27 18:33:38
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team , User loginUser);

    /**
     * 搜索用户
     * @param teamQuery
     * @return
     */
    Page<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin, User loginUser);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散队伍）
     * @param id
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}

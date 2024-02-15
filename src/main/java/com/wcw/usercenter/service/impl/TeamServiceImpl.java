package com.wcw.usercenter.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.mapper.TeamMapper;
import com.wcw.usercenter.model.domain.Team;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.domain.UserTeam;
import com.wcw.usercenter.model.dto.TeamQuery;
import com.wcw.usercenter.model.enums.TeamStatusEnum;
import com.wcw.usercenter.model.request.TeamJoinRequest;
import com.wcw.usercenter.model.request.TeamQuitRequest;
import com.wcw.usercenter.model.request.TeamUpdateRequest;
import com.wcw.usercenter.model.vo.TeamUserVo;
import com.wcw.usercenter.model.vo.UserVo;
import com.wcw.usercenter.service.TeamService;
import com.wcw.usercenter.service.UserService;
import com.wcw.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author wcw
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-01-27 18:33:38
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //请求参数是否为空
        if(team == null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //是否登录，未登录不允许创建
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        //校验信息
        //队伍>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1 || maxNum>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍标题<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        //描述<512
        String description = team.getDescription();
        if(StringUtils.isNotBlank(description) && description.length()>512 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        //status 是否公开（int）不传默认为0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //如果status是加密状态，一定要有密码，且密码<=32
        String password = team.getPassword();
        if(statusEnum.equals(TeamStatusEnum.SECRET) ){
            if(StringUtils.isBlank(password) || password.length()>32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
            }
        }
        //超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间>当前时间");
        }
        //校验用户最多创建5个队伍
        //todo 有bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum>=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }
        //插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        Long teamId = team.getId();
        //插入用户 => 队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery,boolean isAdmin,User loginUser) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if(teamQuery != null){
            Long id = teamQuery.getId();
            if(id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            List<Long> idList = teamQuery.getIdList();
            if(CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id",idList);
            }
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            //查询最大人数相等的
            Integer maxNum = teamQuery.getMaxNum();
            if(maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum",maxNum);
            }
            //根据创建人来查询
            Long userId = teamQuery.getUserId();
            if(userId != null && userId > 0){
                queryWrapper.eq("userId",userId);
            }
            //根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if((!isAdmin || loginUser.getId() != userId)  && TeamStatusEnum.PRIVATE.equals(statusEnum)){
                throw new BusinessException(ErrorCode.NO_AUTH,"没有权限");
            }
            queryWrapper.eq("status",statusEnum.getValue());

        }
        //不展示已过期的队伍
        //expireTime is not null or expireTime > now()
        queryWrapper.and(qw -> qw.isNull("expireTime").or().gt("expireTime",new Date()));
        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        //关联查询创建人的用户信息
        for (Team team: teamList){
            Long userId =team.getUserId();
            if (userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team,teamUserVo);
            //脱敏用户信息
            UserVo userVo = new UserVo();
            if(user != null){
                BeanUtils.copyProperties(user,userVo);
                teamUserVo.setCreateUser(userVo);
            }
            teamUserVoList.add(teamUserVo);
        }
        return teamUserVoList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id == null || id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //只有管理员或者队伍的创建者可以修改
        if(oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if(TeamStatusEnum.SECRET.equals(statusEnum)){
            if(StringUtils.isNotBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须有密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        boolean result = this.updateById(updateTeam);
        return result;
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须加密码");
            }
        }
        //只有一个线程能获取锁
        RLock lock = redissonClient.getLock("yupao:join_team");
        try {
            //抢到锁并执行
            while (true) {
                if(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){
                    //该用户已加入队伍数量
                    Long userId = loginUser.getId();
                    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if (hasJoinNum > 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
                    }
                    //不能重复加入已加入的队伍
                    userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    userTeamQueryWrapper.eq("teamId", teamId);
                    long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经加入");
                    }
                    //已加入队伍的人数
                    long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    //修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("yupao:precachejob:docache:lock", e);
            return  false;
        } finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        //队伍只剩下一人解散
        if(teamHasJoinNum == 1){
            //删除队伍和所有人加入队伍的关系
            this.removeById(teamId);
        }else{
            //队伍还剩下至少两人
            //是队长
            if(team.getUserId() == userId){
                //把队伍转移给最早加入用户
                // 1.查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if(CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeanderId = nextUserTeam.getUserId();
                //更新队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeanderId);
                boolean result = this.updateById(updateTeam);
                if(!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }
        }
    //移除关联
    return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
//        1.校验请求参数
//        2.校验队伍是否存在
        Team team = getTeamById(id);
        Long teamId = team.getId();
//        3.校验你是不是队伍的队长
        if(team.getUserId() != loginUser.getId()){
                throw new BusinessException(ErrorCode.NO_AUTH,"无访问权限");
        }
//        4.移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联信息失败");
        }

//        5.删除队伍
        return this.removeById(teamId);
    }

    /**
     * 根据id 获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId){
        if(teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team ==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        return team;
    }

    /**
     * 获取某队伍人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        //已加入队伍的人数
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}





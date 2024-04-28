package com.wcw.usercenter.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.mapper.FriendsMapper;
import com.wcw.usercenter.model.domain.Friends;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.AddFriendRequest;
import com.wcw.usercenter.model.vo.FriendRecordsVO;
import com.wcw.usercenter.service.FriendsService;
import com.wcw.usercenter.service.UserService;
import com.wcw.usercenter.utils.JSONUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.wcw.usercenter.contant.FriendConstant.*;

/**
* @author wcw
* @description 针对表【friends(好友申请管理表)】的数据库操作Service实现
* @createDate 2024-04-12 22:28:15
*/
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
    implements FriendsService {
    @Resource
    private UserService userService;
    @Resource
    private FriendsMapper friendsMapper;
    /**
     * 申请添加朋友
     * @param loginUser
     * @param addFriendRequest
     * @return
     */
    @Override
    public int addFriendRequest(User loginUser, AddFriendRequest addFriendRequest) {
        Long receiveId = addFriendRequest.getReceiveId();
        String remark = addFriendRequest.getRemark();
        Long userId = loginUser.getId();
        if(ObjectUtils.anyNull(userId,addFriendRequest.getReceiveId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        //校验是不是自己
        if(userId == receiveId){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能向自己发送申请");
        }
        //备注大于100个字
        if(StringUtils.isNotBlank(remark) && remark.length() > 100 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"申请最多为100字");
        }
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromId",userId);
        queryWrapper.eq("receiveId",receiveId);
        List<Friends> friendsList = this.list(queryWrapper);
        friendsList.forEach(friends -> {
            if(friendsList.size() >= 1 && friends.getStatus() == DEFAULT_STATUS){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复申请");
            }
        });
        Friends friends = new Friends();
        friends.setFromId(userId);
        friends.setReceiveId(receiveId);
        if(StringUtils.isBlank(remark)){
            friends.setRemark("我是" + userService.getById(userId).getUsername());
        }else {
            friends.setRemark(remark);
        }
        int insert = friendsMapper.insert(friends);
        return insert;
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public boolean agreeFriends(Long fromId,User loginUser) {
        if(fromId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        /**
         * 查询此用户申请记录
         */
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();;
        queryWrapper.eq("fromId",fromId);
        queryWrapper.eq("receiveId",loginUser.getId());
        List<Friends> friendsList = this.list(queryWrapper);
        if(friendsList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"申请有误");
        }
        User fromUser = userService.getById(fromId);
        User receiveUser = userService.getById(loginUser.getId());
        AtomicBoolean flag = new AtomicBoolean(false);
        friendsList.forEach(friend -> {
            if(friend.getStatus() == DEFAULT_STATUS && DateUtil.between(new Date(), friend.getCreateTime(), DateUnit.DAY) <= 3 ) {
                Set<Long> fromUserIds = JSONUtils.stringJsonListToLongSet(fromUser.getFriendIds());
                Set<Long> receiveUserIds = JSONUtils.stringJsonListToLongSet(receiveUser.getFriendIds());
                fromUserIds.add(receiveUser.getId());
                receiveUserIds.add(fromUser.getId());
                Gson gson = new Gson();
                String jsonFromUserUserIds = gson.toJson(fromUserIds);
                String jsonReceiveUserIds = gson.toJson(receiveUserIds);
                receiveUser.setFriendIds(jsonReceiveUserIds);
                fromUser.setFriendIds(jsonFromUserUserIds);
                friend.setStatus(AGREE_STATUS);
                userService.updateById(fromUser);
                userService.updateById(receiveUser);
                // 2. 修改状态由0改为1
                friend.setStatus(AGREE_STATUS);
                boolean result = this.updateById(friend);
                flag.set(result);
            }
        });
        return flag.get();
    }
    /**
     * 获取该用户申请记录（发出）
     * @param loginUser
     * @return
     */
    @Override
    public List<FriendRecordsVO> getFriendsRecords(User loginUser) {
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromId",loginUser.getId());
        List<Friends> friendsRecordList = this.list(queryWrapper);
        List<FriendRecordsVO> recordsVOList = friendsRecordList.stream().map(friends -> {
            FriendRecordsVO friendRecordsVO = new FriendRecordsVO();
            BeanUtils.copyProperties(friends, friendRecordsVO);
            User applyUser = userService.getById(friends.getReceiveId());
            friendRecordsVO.setApplyUser(userService.getSafetyUser(applyUser));
            return friendRecordsVO;
        }).collect(Collectors.toList());
        return recordsVOList;
    }

    /**
     * 获取朋友记录（接收）
     * @param loginUser
     * @return
     */
    @Override
    @Transactional
    public List<FriendRecordsVO> obtainFriendApplicationRecords(User loginUser) {
        // 查询出当前用户接收所有申请、同意记录
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getReceiveId, loginUser.getId());
        List<Friends> friendsList = this.list(friendsLambdaQueryWrapper);
        Collections.reverse(friendsList);
        return friendsList.stream().map(friend -> {
            FriendRecordsVO friendRecordsVO = new FriendRecordsVO();
            BeanUtils.copyProperties(friend, friendRecordsVO);
            User user = userService.getById(friend.getFromId());
            friendRecordsVO.setApplyUser(userService.getSafetyUser(user));
            if(friend.getIsRead() == NOT_READ){
                friend.setIsRead(READ);
            }
            this.updateById(friend);
            return friendRecordsVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getRecordCount(User loginUser) {
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getReceiveId, loginUser.getId());
        List<Friends> friendsList = this.list(friendsLambdaQueryWrapper);
        int count = 0;
        for (Friends friend : friendsList) {
            if (friend.getStatus() == DEFAULT_STATUS && friend.getIsRead() == NOT_READ) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean toRead(User loginUser, Set<Long> ids) {
        boolean flag = false;
        for (Long id : ids) {
            Friends friend = this.getById(id);
            if (friend.getStatus() == DEFAULT_STATUS && friend.getIsRead() == NOT_READ) {
                friend.setIsRead(READ);
                flag = this.updateById(friend);
            }
        }
        return flag;
    }

    /**
     * 取消申请
     * @param id
     * @param loginUser
     * @return
     */
    @Override
    public boolean canceledApply(Long id, User loginUser) {
        Friends friend = this.getById(id);
        if (friend.getStatus() != DEFAULT_STATUS) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该申请已过期或已通过");
        }
        friend.setStatus(REVOKE_STATUS);
        return this.updateById(friend);
    }
}





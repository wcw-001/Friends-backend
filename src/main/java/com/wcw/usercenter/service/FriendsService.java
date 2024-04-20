package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.Friends;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.AddFriendRequest;
import com.wcw.usercenter.model.vo.FriendRecordsVO;

import java.util.List;
import java.util.Set;


/**
* @author wcw
* @description 针对表【friends(好友申请管理表)】的数据库操作Service
* @createDate 2024-04-12 22:28:15
*/
public interface FriendsService extends IService<Friends> {
    /**
     * 申请添加朋友
     * @param loginUser
     * @param addFriendRequest
     * @return
     */
    int addFriendRequest(User loginUser, AddFriendRequest addFriendRequest);

    /**
     * 同意好友申请
     * @param loginUser
     * @param fromId
     * @return
     */
    boolean agreeFriends(Long fromId,User loginUser);

    /**
     *获取我的申请记录
     * @param loginUser
     * @return
     */
    List<FriendRecordsVO> getFriendsRecords(User loginUser);

    /**
     * 获取朋友记录
     * @param loginUser
     * @return
     */
    List<FriendRecordsVO> obtainFriendApplicationRecords(User loginUser);

    /**
     * 获取未读申请数量
     * @param loginUser
     * @return
     */
    int getRecordCount(User loginUser);
    /**
     * 读取纪录
     *
     * @param loginUser
     * @param ids
     * @return
     */
    boolean toRead(User loginUser, Set<Long> ids);

    /**
     * 取消申请
     * @param id
     * @param loginUser
     * @return
     */
    boolean canceledApply(Long id, User loginUser);
}

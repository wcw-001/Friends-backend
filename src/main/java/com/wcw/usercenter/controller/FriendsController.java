package com.wcw.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wcw.usercenter.common.BaseResponse;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.common.ResultUtils;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.model.domain.Friends;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.AddFriendRequest;
import com.wcw.usercenter.model.vo.FriendRecordsVO;
import com.wcw.usercenter.service.FriendsService;
import com.wcw.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/friend")
public class FriendsController {
    @Resource
    UserService userService;
    @Resource
    FriendsService friendsService;

    /**
     * 申请添加朋友
     * @param addFriendRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Integer> addFriendsRecords(AddFriendRequest addFriendRequest, HttpServletRequest request){
        if(addFriendRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求有误");
        }
        log.info("addFriendRequest:{}",addFriendRequest);
        User loginUser = userService.getLoginUser(request);
        int result = friendsService.addFriendRequest(loginUser,addFriendRequest);
        return ResultUtils.success(result);
    }
    @PostMapping("/agree/{fromId}")
    public BaseResponse<Boolean> agreeFriends(@PathVariable("fromId") Long fromId, HttpServletRequest request){
        if(fromId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求有误");
        }
        log.info("fromId:{}",fromId);
        User loginUser = userService.getLoginUser(request);
        boolean result = friendsService.agreeFriends(fromId,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * （发出）
     * @param request
     * @return
     */
    @GetMapping("/getMyRecords")
    public BaseResponse<List<FriendRecordsVO>> getFriendsRecords(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        List<FriendRecordsVO> friendRecordsVO = friendsService.getFriendsRecords(loginUser);
        return ResultUtils.success(friendRecordsVO);
    }

    /**
     *获取朋友申请记录
     * friendRecordsVO
     * @param request
     * @return
     */
    @GetMapping("getRecords")
    public BaseResponse<List<FriendRecordsVO>> getRecords(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<FriendRecordsVO> friendsList = friendsService.obtainFriendApplicationRecords(loginUser);
        return ResultUtils.success(friendsList);
    }

    /**
     * 获取未读申请数量
     * @param request
     * @return
     */
    @GetMapping("getRecordCount")
    public BaseResponse<Integer> getRecordCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int recordCount = friendsService.getRecordCount(loginUser);
        return ResultUtils.success(recordCount);
    }
    @GetMapping("/read")
    public BaseResponse<Boolean> toRead(@RequestParam(required = false) Set<Long> ids, HttpServletRequest request) {
        if (CollectionUtils.isEmpty(ids)) {
            return ResultUtils.success(false);
        }
        User loginUser = userService.getLoginUser(request);
        boolean isRead = friendsService.toRead(loginUser, ids);
        return ResultUtils.success(isRead);
    }
    @PostMapping("canceledApply/{id}")
    public BaseResponse<Boolean> canceledApply(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求有误");
        }
        User loginUser = userService.getLoginUser(request);
        boolean canceledApplyStatus = friendsService.canceledApply(id, loginUser);
        return ResultUtils.success(canceledApplyStatus);
    }
    /**
     * 按状态搜索好友列表
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */
    @GetMapping("/my/list")
    public BaseResponse<List<User>> searchUsersByUserName(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long loginUserId = loginUser.getId();
        List<Friends> friendsList = friendsService.list(new QueryWrapper<Friends>()
                .eq("receiveId", loginUserId)
                .eq("status", 1));
        // 使用流和Lambda表达式进行过滤查询
        List<User> userList = friendsList.stream().map(friends -> userService.getById(friends.getFromId())).collect(Collectors.toList());

        return ResultUtils.success(userList);
    }
}

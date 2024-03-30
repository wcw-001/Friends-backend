package com.wcw.usercenter.controller;

import com.wcw.usercenter.common.BaseResponse;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.common.ResultUtils;
import com.wcw.usercenter.contant.ChatConstant;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.ChatRequest;
import com.wcw.usercenter.model.vo.ChatMessageVO;
import com.wcw.usercenter.service.ChatService;
import com.wcw.usercenter.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    /**
     * 聊天服务
     */
    @Resource
    private ChatService chatService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 私聊
     * @param chatRequest
     * @param request
     * @return
     */
    @PostMapping("/privateChat")
    @ApiOperation(value = "获取私聊")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "chatRequest", value = "聊天请求"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getPrivateChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> privateChat = chatService.getPrivateChat(chatRequest, ChatConstant.PRIVATE_CHAT, loginUser);
        return ResultUtils.success(privateChat);
    }
    /**
     * 团队聊天
     *
     * @param chatRequest 聊天请求
     * @param request     请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @PostMapping("/teamChat")
    @ApiOperation(value = "获取队伍聊天")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "chatRequest", value = "聊天请求"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getTeamChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        if (chatRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求有误");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> teamChat = chatService.getTeamChat(chatRequest, ChatConstant.TEAM_CHAT, loginUser);
        return ResultUtils.success(teamChat);
    }

    /**
     * 大厅聊天
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link ChatMessageVO}>>
     */
    @GetMapping("/hallChat")
    @ApiOperation(value = "获取大厅聊天")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<List<ChatMessageVO>> getHallChat(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        List<ChatMessageVO> hallChat = chatService.getHallChat(ChatConstant.HALL_CHAT, loginUser);
        return ResultUtils.success(hallChat);
    }
}

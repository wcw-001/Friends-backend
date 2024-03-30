package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.Chat;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.ChatRequest;
import com.wcw.usercenter.model.vo.ChatMessageVO;

import java.util.Date;
import java.util.List;

/**
* @author wcw
* @description 针对表【chat(聊天消息表)】的数据库操作Service
* @createDate 2024-03-02 21:50:15
*/
public interface ChatService extends IService<Chat> {
     List<ChatMessageVO> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser);

     List<ChatMessageVO> getCache(String redisKey, String id);

     void saveCache(String redisKey, String id, List<ChatMessageVO> chatMessageVos);

     ChatMessageVO chatResult(Long userId, Long toId, String text, Integer chatType, Date createTime);

     void deleteKey(String key, String id);

    List<ChatMessageVO> getTeamChat(ChatRequest chatRequest, int teamChat, User loginUser);

    List<ChatMessageVO> getHallChat(int chatType, User loginUser);
}

package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.Message;
import com.wcw.usercenter.model.vo.MessageVO;

import java.util.List;

/**
* @author wcw
* @description 针对表【message】的数据库操作Service
* @createDate 2023-06-21 17:39:30
*/
public interface MessageService extends IService<Message> {

    long getMessageNum(Long userId);

    long getLikeNum(Long userId);

    List<MessageVO> getLike(Long userId);


    Boolean hasNewMessage(Long userId);
}

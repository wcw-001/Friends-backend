package com.wcw.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcw.usercenter.model.domain.Chat;
import org.apache.ibatis.annotations.Mapper;

/**
* @author wcw
* @description 针对表【chat(聊天消息表)】的数据库操作Mapper
* @createDate 2023-06-17 21:50:15
* @Entity com.wcw.model.domain.Chat
*/
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

}





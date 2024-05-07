package com.wcw.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcw.usercenter.model.domain.CommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Shier
* @description 针对表【comment_like】的数据库操作Mapper
* @createDate 2023-06-08 16:24:28
* @Entity com.shier.model.domain.CommentLike
*/
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {

}





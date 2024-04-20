package com.wcw.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcw.usercenter.model.domain.BlogLike;
import org.apache.ibatis.annotations.Mapper;

/**
* @author wcw
* @description 针对表【blog_like】的数据库操作Mapper
* @createDate 2023-06-05 21:54:55
* @Entity com.shier.model.domain.BlogLike
*/
@Mapper
public interface BlogLikeMapper extends BaseMapper<BlogLike> {

}





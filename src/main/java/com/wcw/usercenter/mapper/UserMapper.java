package com.wcw.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcw.usercenter.model.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * 接口服务
 * @author wcw
 *
 */
@Mapper
//@Repository
public interface UserMapper extends BaseMapper<User> {

}





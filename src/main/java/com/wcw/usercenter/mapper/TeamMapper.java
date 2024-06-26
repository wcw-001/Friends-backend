package com.wcw.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcw.usercenter.model.domain.Team;
import org.apache.ibatis.annotations.Mapper;

/**
* @author wcw
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2024-01-27 18:33:38
* @Entity generator.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

}





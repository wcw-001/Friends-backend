package com.wcw.usercenter.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户信息封装类（脱敏）
 * @author wcw
 */
@Data
public class TeamUserVo implements Serializable {
    private static final long serialVersionUID = 9060368554152825001L;
    /**
     * 队伍id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     *更新时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 创建人用户列表
     */
    private UserVo createUser;
    /**
     * 加入的用户数
     */
    private Integer hasJoinNum;
    /**
     *是否已经加入
     */
    private boolean hasJoin = false;
}

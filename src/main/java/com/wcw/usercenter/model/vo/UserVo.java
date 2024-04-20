package com.wcw.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户包装类（脱敏）
 *
 * @TableName user
 */
@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = 3177942915716861876L;
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;
    /**
     * 个人简介
     */
    private String profile;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 用户角色 0- 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 标签列表json
     */
    private String tags;
    /**
     * 是否关注
     */
    @ApiModelProperty(value = "是否关注")
    private Boolean isFollow;

}

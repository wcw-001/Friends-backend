package com.wcw.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author wcw
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -7552538814772320741L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}

package com.wcw.usercenter.model.request;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户加入队伍
 * @author wcw
 */
@Data
public class TeamJoinRequest implements Serializable {
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

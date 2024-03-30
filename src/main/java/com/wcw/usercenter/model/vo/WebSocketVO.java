package com.wcw.usercenter.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 网络套接字签证官
 *
 * @author wcw
 */
@Data
public class WebSocketVO implements Serializable {

    private static final long serialVersionUID = 4696612253320170315L;

    private long id;

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

}

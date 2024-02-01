package com.wcw.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author wcw
 */
@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 4162878263392014970L;
    private String userAccount;
    private String userPassword;

}

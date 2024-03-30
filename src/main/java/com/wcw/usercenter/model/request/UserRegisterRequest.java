package com.wcw.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author wcw
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 7332769468663998038L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String userCode;
    private String phone;

}

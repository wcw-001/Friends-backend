package com.wcw.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的删除请求参数
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = -6276546550080205806L;
    private long id;
}

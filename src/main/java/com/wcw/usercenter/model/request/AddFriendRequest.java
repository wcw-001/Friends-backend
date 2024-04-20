package com.wcw.usercenter.model.request;

import lombok.Data;

@Data
public class AddFriendRequest {
    private Long receiveId;
    private String remark;
}

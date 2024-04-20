package com.wcw.usercenter.model.vo;

import com.wcw.usercenter.model.domain.User;
import lombok.Data;

@Data
public class FriendRecordsVO {
    private Long id;
    private Integer status;
    private String remark;
    private User applyUser;
}

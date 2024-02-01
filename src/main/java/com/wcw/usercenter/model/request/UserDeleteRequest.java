package com.wcw.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserDeleteRequest implements Serializable {
  private static final long serialVersionUID = -2269150972913258658L;
  private Long id;


}

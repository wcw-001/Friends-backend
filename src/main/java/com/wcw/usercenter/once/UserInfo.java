package com.wcw.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户信息表格
 */

@Data
public class UserInfo {
    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String userCode;
    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}

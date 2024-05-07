package com.wcw.usercenter.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客添加请求
 *
 * @author wcw
 */
@Data
public class CommentAddRequest implements Serializable {
    /**
     * 探店id
     */
    private Long blogId;
    /**
     * 回复的内容
     */
    private String content;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

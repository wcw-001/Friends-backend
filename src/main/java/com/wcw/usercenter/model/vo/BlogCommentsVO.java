package com.wcw.usercenter.model.vo;

import com.wcw.usercenter.model.domain.BlogComments;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class BlogCommentsVO extends BlogComments implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 5695588849785352130L;
    /**
     * 用户评论
     */
    @ApiModelProperty(value = "评论用户")
    private UserVo commentUser;
    /**
     * 是喜欢
     */
    @ApiModelProperty(value = "是否点赞")
    private Boolean isLiked;
    /**
     * 博客
     */
    @ApiModelProperty(value = "博客")
    private BlogVO blog;
}

package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.BlogComments;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.CommentAddRequest;
import com.wcw.usercenter.model.vo.BlogCommentsVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author wcw
* @description 针对表【tb_blog_comments】的数据库操作Service
* @createDate 2024-05-05 17:29:15
*/
public interface TbBlogCommentsService extends IService<BlogComments> {

    void addBlog(CommentAddRequest commentAddRequest, User loginUser);

    /**
     * 获得评论列表
     * @param blogId
     * @return
     */
    List<BlogCommentsVO> listComments(long blogId);

    @Transactional
    void likeComment(long commentId, Long userId);

    /**
     * 删除评论
     * @param id
     * @param id1
     * @param isAdmin
     */
    void deleteComment(Long id, Long userId, boolean isAdmin);

    List<BlogCommentsVO> listMyComments(long userId,long blogId);
}

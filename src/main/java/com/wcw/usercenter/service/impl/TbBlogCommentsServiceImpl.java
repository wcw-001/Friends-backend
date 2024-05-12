package com.wcw.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.mapper.TbBlogCommentsMapper;
import com.wcw.usercenter.model.domain.*;
import com.wcw.usercenter.model.enums.MessageTypeEnum;
import com.wcw.usercenter.model.request.CommentAddRequest;
import com.wcw.usercenter.model.vo.BlogCommentsVO;
import com.wcw.usercenter.model.vo.UserVo;
import com.wcw.usercenter.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wcw.usercenter.contant.RedisConstants.MESSAGE_LIKE_NUM_KEY;

/**
* @author wcw
* @description 针对表【tb_blog_comments】的数据库操作Service实现
* @createDate 2024-05-05 17:29:15
*/
@Service
public class TbBlogCommentsServiceImpl extends ServiceImpl<TbBlogCommentsMapper, BlogComments>
    implements TbBlogCommentsService {
    @Resource
    BlogService blogService;
    @Resource
    UserService userService;
    @Resource
    CommentLikeService commentLikeService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    MessageService messageService;

    @Override
    @Transactional
    public void addBlog(CommentAddRequest commentAddRequest, User loginUser) {
        BlogComments blogComments = new BlogComments();
        blogComments.setUserId(loginUser.getId());
        blogComments.setBlogId(commentAddRequest.getBlogId());
        blogComments.setContent(commentAddRequest.getContent());
        blogComments.setStatus(0);
        blogComments.setLiked(0);
        this.save(blogComments);
        Long blogId = commentAddRequest.getBlogId();
        Blog blog = blogService.getById(blogId);
        blog.setCommentsNum(blog.getCommentsNum() + 1);
        blogService.updateById(blog);
    }

    @Override
    public List<BlogCommentsVO> listComments(long blogId) {
        //查出博客的所有评论
        QueryWrapper<BlogComments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blogId", blogId);
        List<BlogComments> blogComments = this.list(queryWrapper);
        if (blogComments == null && blogComments.size() == 0) {
            return Collections.emptyList();
        }
        List<BlogCommentsVO> result = blogComments.stream().map(blogComment -> {
            BlogCommentsVO blogCommentsVO = new BlogCommentsVO();
            BeanUtils.copyProperties(blogComment, blogCommentsVO);
            Long userId = blogComment.getUserId();
            User user = userService.getById(userId);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            blogCommentsVO.setCommentUser(userVo);
            QueryWrapper<CommentLike> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("userId", userId).eq("commentId", blogComment.getId());
            long count = commentLikeService.count(queryWrapper1);
            blogCommentsVO.setIsLiked(count > 0);
            return blogCommentsVO;
        }).collect(Collectors.toList());
        return result;
    }


    @Override
    @Transactional
    public void likeComment(long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> commentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLikeLambdaQueryWrapper.eq(CommentLike::getCommentId, commentId).eq(CommentLike::getUserId, userId);
        long count = commentLikeService.count(commentLikeLambdaQueryWrapper);
        if (count == 0) {
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);
            commentLikeService.save(commentLike);
            BlogComments blogComments = this.getById(commentId);
            this.update().eq("id", commentId)
                    .set("likedNum", blogComments.getLiked() + 1)
                    .update();
            String likeNumKey = MESSAGE_LIKE_NUM_KEY + blogComments.getUserId();
            Boolean hasKey = stringRedisTemplate.hasKey(likeNumKey);
            if (Boolean.TRUE.equals(hasKey)) {
                stringRedisTemplate.opsForValue().increment(likeNumKey);
            } else {
                stringRedisTemplate.opsForValue().set(likeNumKey, "1");
            }
            Message message = new Message();
            message.setType(MessageTypeEnum.BLOG_COMMENT_LIKE.getValue());
            message.setFromId(userId);
            message.setToId(blogComments.getUserId());
            message.setData(String.valueOf(blogComments.getId()));
            messageService.save(message);
        } else {
            commentLikeService.remove(commentLikeLambdaQueryWrapper);
            BlogComments blogComments = this.getById(commentId);
            this.update().eq("id", commentId)
                    .set("likedNum", blogComments.getLiked() - 1)
                    .update();
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId, boolean isAdmin) {
        BlogComments comments = this.getById(id);
        if (comments == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }
        if(!isAdmin || !comments.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限");
        }
        this.removeById(id);
        Blog blog = blogService.getById(comments.getBlogId());
        blog.setCommentsNum(blog.getCommentsNum() - 1);
        blogService.updateById(blog);
    }

    @Override
    public List<BlogCommentsVO> listMyComments(long userId,long blogId) {
        //查出博客的所有评论
        QueryWrapper<BlogComments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blogId", blogId);
        List<BlogComments> blogComments = this.list(queryWrapper);
        if (blogComments == null && blogComments.size() == 0) {
            return Collections.emptyList();
        }
        List<BlogCommentsVO> result = blogComments.stream().map(blogComment -> {
            BlogCommentsVO blogCommentsVO = new BlogCommentsVO();
            BeanUtils.copyProperties(blogComment, blogCommentsVO);
            //Long commentUserId = blogComment.getUserId();
            User user = userService.getById(userId);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            blogCommentsVO.setCommentUser(userVo);
            QueryWrapper<CommentLike> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("userId", userId).eq("commentId", blogComment.getId());
            long count = commentLikeService.count(queryWrapper1);
            blogCommentsVO.setIsLiked(count > 0);
            return blogCommentsVO;
        }).collect(Collectors.toList());
        return result;
    }
}

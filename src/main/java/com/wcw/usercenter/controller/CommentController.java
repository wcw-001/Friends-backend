package com.wcw.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcw.usercenter.common.BaseResponse;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.common.ResultUtils;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.manager.RedisLimiterManager;
import com.wcw.usercenter.model.domain.Blog;
import com.wcw.usercenter.model.domain.BlogComments;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.CommentAddRequest;
import com.wcw.usercenter.model.vo.BlogCommentsVO;
import com.wcw.usercenter.model.vo.BlogVO;
import com.wcw.usercenter.service.TbBlogCommentsService;
import com.wcw.usercenter.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    /**
     * 博客服务
     */
    @Resource
    private TbBlogCommentsService commentService;

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    /**
     * 获取评论列表
     * @param request     请求
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取评论")
    public BaseResponse<List<BlogCommentsVO>> listBlogPage(long blogId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if( blogId <= 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<BlogCommentsVO> commentsList = commentService.listComments(blogId);
        return ResultUtils.success(commentsList);
    }

    /**
     * 发表评论
     *
     * @param commentAddRequest 博客添加请求
     * @param request        请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @PostMapping("/add")
    public BaseResponse<String> addBlog(CommentAddRequest commentAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 限流
        boolean doRateLimit = redisLimiterManager.doRateLimit(loginUser.getId().toString());
        if (!doRateLimit) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        if (commentAddRequest.getBlogId() == null || StringUtils.isBlank(commentAddRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        commentService.addBlog(commentAddRequest, loginUser);
//        bloomFilter.add(BLOG_BLOOM_PREFIX + blogId);
        return ResultUtils.success("添加成功");
    }
    /**
     * 喜欢评论
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @PutMapping("/like/{id}")
    @ApiOperation(value = "点赞博文评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "博文评论id"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<String> likeComment(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        commentService.likeComment(id, loginUser.getId());
        return ResultUtils.success("ok");
    }
    /**
     * 删除博客评论
     *
     * @param id      id
     * @param request 请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除评论")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "博文评论id"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<String> deleteBlogComment(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean isAdmin = userService.isAdmin(loginUser);
        commentService.deleteComment(id, loginUser.getId(), isAdmin);
        return ResultUtils.success("ok");
    }
    /**
     * 获取评论列表
     * @param request     请求
     */
    @GetMapping("/list/my")
    @ApiOperation(value = "获取评论")
    public BaseResponse<List<BlogCommentsVO>> listMyBlogPage(long blogId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if( blogId <= 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<BlogCommentsVO> commentsList = commentService.listMyComments(loginUser.getId(),blogId);
        return ResultUtils.success(commentsList);
    }



}

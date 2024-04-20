package com.wcw.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.model.domain.Blog;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.BlogAddRequest;
import com.wcw.usercenter.model.request.BlogUpdateRequest;
import com.wcw.usercenter.model.vo.BlogVO;

/**
* @author Shier
* @description 针对表【blog】的数据库操作Service
* @createDate 2023-06-03 15:54:34
*/
public interface BlogService extends IService<Blog> {

    Long addBlog(BlogAddRequest blogAddRequest, User loginUser);

    Page<BlogVO> listMyBlogs(long currentPage, Long id);

    void likeBlog(long blogId, Long userId);

    Page<BlogVO> pageBlog(long currentPage,String title, Long id);

    BlogVO getBlogById(long blogId, Long userId);

    void deleteBlog(Long blogId, Long userId, boolean isAdmin);

    void updateBlog(BlogUpdateRequest blogUpdateRequest, Long userId, boolean isAdmin);
}

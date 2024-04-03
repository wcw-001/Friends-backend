package com.wcw.usercenter.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcw.usercenter.common.BaseResponse;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.UserUpdatePasswordRequest;
import com.wcw.usercenter.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author wcw
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-08-02 13:11:42
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String phone,String userAccount, String userPassword, String checkPassword);

    /**
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @return 脱敏用户信息
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 修改密码
     * @param updatePasswordRequest
     * @param request
     * @return
     */
    boolean updateUserPassword(UserUpdatePasswordRequest updatePasswordRequest, HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagList
     * @return
     */
    List<User> searchUsersByTag(List<String> tagList);

    /**
     * 伙伴匹配系统更新用户信息
     * @param user
     * @return
     */
    Integer updateUser(User user,User loginUser);
    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);
    int userLogout(HttpServletRequest request);

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */

    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);

    /**
     * 修改密码
     * @param phone
     * @param password
     * @param confirmPassword
     */
    void updatePassword(String phone, String password, String confirmPassword);

    /**
     * 根据id获取用户
     * @param userId
     * @param loginUserId
     * @return
     */
    UserVo getUserById(Long userId, Long loginUserId);

    List<String> getUserTags(Long id);

    void updateTags(List<String> tags, Long id);


}

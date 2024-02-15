package com.wcw.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.wcw.usercenter.common.ErrorCode;
import com.wcw.usercenter.exception.BusinessException;
import com.wcw.usercenter.exception.ThrowUtils;
import com.wcw.usercenter.mapper.UserMapper;
import com.wcw.usercenter.model.domain.User;
import com.wcw.usercenter.model.request.UserUpdatePasswordRequest;
import com.wcw.usercenter.model.vo.UserVo;
import com.wcw.usercenter.service.UserService;
import com.wcw.usercenter.utils.AlgorithmUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wcw.usercenter.contant.UserConstant.ADMIN_RILE;
import static com.wcw.usercenter.contant.UserConstant.USER_LOGIN_STATE;


/**
 * @author wcw
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-08-02 13:11:42
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值,将密码进行混淆
     * */
    private static final String SALT = "wcw";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String userCode) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号太短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if(userCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号过长");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }
        //编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userCode", userCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }
        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserCode(userCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }

        return user.getId();
    }


    /**
     * 用户登录
     * @author wcw
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param request
     * @return
     */
    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 3) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }

        //账户不能包含特殊字符
        String validPattern = ".*[\\s`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR,"不存在该用户");
        }
        //用户脱敏
        User safetyUser = getSafetyUser(user);

        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    @Override
    public boolean updateUserPassword(UserUpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        if (updatePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = getLoginUser(request);
        Long userId = loginUser.getId();
        if (userId < 0 || userId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "不存在该用户");
        }
        User user = new User();
        BeanUtils.copyProperties(updatePasswordRequest, user);
        user.setId(loginUser.getId());

        // 使用 MD5 加密新密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + updatePasswordRequest.getNewPassword()).getBytes());
        user.setUserPassword(encryptedPassword);
        if (encryptedPassword.equals(updatePasswordRequest.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改密码不能相同");
        }
        boolean result = updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return true;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
        public User getSafetyUser(User originUser){
            if(originUser ==null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未登录");
            }
            User safetyUser = new User();
            safetyUser.setId(originUser.getId());
            safetyUser.setUsername(originUser.getUsername());
            safetyUser.setUserAccount(originUser.getUserAccount());
            safetyUser.setAvatarUrl(originUser.getAvatarUrl());
            safetyUser.setGender(originUser.getGender());
            safetyUser.setProfile(originUser.getProfile());
            safetyUser.setPhone(originUser.getPhone());
            safetyUser.setEmail(originUser.getEmail());
            safetyUser.setUserCode(originUser.getUserCode());
            safetyUser.setUserStatus(originUser.getUserStatus());
            safetyUser.setCreateTime(originUser.getCreateTime());
            safetyUser.setUserRole(originUser.getUserRole());
            safetyUser.setTags(originUser.getTags());
            return safetyUser;
        }

    /**
     * 判断是否登录
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        /**
         * 判断请求是否为空
         */
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        return currentUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户
     *
     * @param tagList 用户要拥有的标签
     * @return
     */
    /*@Deprecated
    @Override
    public List<User> searchUsersByTag(List<String> tagList) {
        *//**
         * 1.直接用sql查询
         *//*
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接 and 查询
        //用queryWrapper来限制查询条件
        for (String tagName : tagList) {
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser ).collect(Collectors.toList());
    }*/
    /**
     * 根据标签搜索用户(内存过滤)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTag(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        /**
         * 2.在内存中查询
         */
        //1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> temTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : tagNameList) {
                if (!temTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 伙伴匹配系统更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public Integer updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //todo 补充更多校验，如果用户没有直接传任何要更新的值，就直接报错，不用执行更新语句
        //仅管理员和自己可见
        //如果是管理员，允许更新任意用户
        //如果不是管理员，允许用户更新任意用户
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User oldUser = userMapper.selectById(userId);
        if(oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);

    }
    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request){
        //仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_RILE;
    }
    @Override
    public boolean isAdmin(User loginUser){

        return loginUser != null && loginUser.getUserRole() == ADMIN_RILE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //用户列表的下表 => 相似度
        List<Pair<User,Long>> list = new ArrayList<>();
        //依次计算所有用户和当前用户的相似度
        for(int i =0;i < userList.size();i++){
            User user = userList.get(i);
            String userTags = user.getTags();
            //无标签或者为当前用户自己
            if(StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()){
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags,new TypeToken<List<String>>(){}.getType());
            //计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user,distance));
        }
        //按编辑距离由小到大排序
        List<Pair<User,Long>> topUserPairList = list.stream()
                .sorted((a,b) -> (int) (a.getValue()-b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        //原本顺序 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id",userIdList);
        Map<Long,List<User>> userIdUserListMap = this.list(userQueryWrapper).stream().map(user -> getSafetyUser(user)).collect(Collectors.groupingBy(User::getId));
        List<User> finallUserList = new ArrayList<>();
        for(Long userId : userIdList){
            finallUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finallUserList;

    }


}






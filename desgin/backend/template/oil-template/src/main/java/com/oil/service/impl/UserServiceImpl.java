package com.oil.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oil.constant.*;
import com.oil.dto.UserDTO;
import com.oil.dto.UserLoginDTO;
import com.oil.dto.UserPageQueryDTO;
import com.oil.entity.User;
import com.oil.exception.*;
import com.oil.mapper.postgres.UserMapper;
import com.oil.properties.JwtProperties;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.UserService;
import com.oil.utils.JwtUtil;
import com.oil.vo.UserInfoVo;
import com.oil.vo.UserLoginVO;
import com.oil.vo.UserPageQueryVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ClassName: UserServiceImpl
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/25
 * @Version 1.0
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void insertUserToRedis(){
        log.info("将用户的状态及删除键放入缓存...");
        for (User user : list()) {
            stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_KEY + RedisConstant.STATUS_KEY + user.getId(), user.getIsEnabled().toString());
            stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_KEY + RedisConstant.DELETE_KEY + user.getId(), user.getIsDeleted().toString());
        }
    }

    @Override
    public Result<UserLoginVO> login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        User user = query().eq("username", username).one();
        if (user==null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!user.getIsDeleted().equals(StatusConstant.DISABLE)){
            throw new AccountLockedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (user.getIsEnabled().equals(UserConstant.DISABLED)){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            log.error("密码错误！正确的密码是:{}",password);
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(username)
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    @Override
    public Result<UserInfoVo> info(String token) {
        if (token==null || token.equals("")){
            throw new UserException(UserConstant.TOKEN_INVALID);
        }
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
        if (claims == null){
            throw new UserException(UserConstant.TOKEN_INVALID);
        }
        Integer id = claims.get(JwtClaimsConstant.USER_ID, Integer.class);
        User user = getById(id);
        if (user == null){
            throw new UserException(UserConstant.USER_NOT_FOUND);
        }
        List<String> roles = List.of(Integer.valueOf(1).equals(user.getPermission()) ? UserConstant.ADMIN : UserConstant.EDITOR);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setAvatar(UserConstant.AVATAR);
        userInfoVo.setName(user.getUsername());
        userInfoVo.setRoles(roles);
        return Result.success(userInfoVo);
    }

    @Override
    public Result<PageResult> page(UserPageQueryDTO userPageQueryDTO) {
        if (userPageQueryDTO==null){
            throw new ParamNotFoundException(UserConstant.USER_PAGE_QUERY_NOT_EXIST);
        }
        Page<User> page = new Page<>(userPageQueryDTO.getPage(), userPageQueryDTO.getPageSize());
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getIsDeleted, StatusConstant.DISABLE)
            .eq(null!=userPageQueryDTO.getCompany() && !"".equals(userPageQueryDTO.getCompany()),
                    User::getCompany, userPageQueryDTO.getCompany());
        lqw.and(null!=userPageQueryDTO.getInformation() && !"".equals(userPageQueryDTO.getInformation()),
                w->w.like(User::getUsername, userPageQueryDTO.getInformation())
                        .or()
                        .like(User::getCompany, userPageQueryDTO.getInformation()))
                .orderByAsc(User::getId);
        page = userMapper.selectPage(page, lqw);
        List<User> userList = page.getRecords();
        List<UserPageQueryVO> userPageQueryVOList = new ArrayList<>();
        if (page.getTotal()==0 || userList.size()==0){
            return Result.success(new PageResult(page.getTotal(), userPageQueryVOList));
        }
        for (User user : userList) {
            UserPageQueryVO userPageQueryVO = new UserPageQueryVO();
            BeanUtils.copyProperties(user, userPageQueryVO);
            userPageQueryVOList.add(userPageQueryVO);
        }
        return Result.success(new PageResult(page.getTotal(), userPageQueryVOList));
    }

    @Override
    public Result<Set<String>> company() {
        String[] companyList = userMapper.getCompanyList();
        if (companyList==null || companyList.length==0){
            throw new UserException(MessageConstant.COMPANY_NOT_FOUND);
        }
        // 对找到的活动区域进行去重
        Set<String> companySet = new HashSet<>(Arrays.asList(companyList));
        return Result.success(companySet);
    }

    @Override
    public Result addUser(UserDTO userDTO) {
        if (userDTO==null){
            throw new ParamNotFoundException(UserConstant.ADDED_USER_IS_NULL);
        }
        LocalDateTime now = LocalDateTime.now();
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(null != userDTO.getUsername(), User::getUsername, userDTO.getUsername()));
        if (user!=null){
            throw new UserException(MessageConstant.ALREADY_EXIST);
        }
        String password = DigestUtils.md5DigestAsHex(UserConstant.DEFAULT_PASSWORD.getBytes());
        User addUser = User.builder()
                .username(userDTO.getUsername())
                .password(password)
                .company(userDTO.getCompany())
                .gender(UserConstant.MALE)
                .permission(UserConstant.EDITOR_PERMISSION)
                .isDeleted(StatusConstant.DISABLE)
                .isEnabled(UserConstant.ENABLED)
                .updateTime(now)
                .createTime(now)
                .build();
        save(addUser);
        // 将新增的用户状态以及删除键加入到缓存中
        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_KEY + RedisConstant.STATUS_KEY + addUser.getId(), addUser.getIsEnabled().toString());
        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_KEY + RedisConstant.DELETE_KEY + addUser.getId(), addUser.getIsDeleted().toString());
        return Result.success();
    }

    @Override
    public Result deleteByUsernameList(List<String> usernameList) {
        if (usernameList == null || usernameList.isEmpty()){
            throw new ParamNotFoundException(UserConstant.USERNAME_LIST_IS_EMPTY);
        }
//        userMapper.deleteByUsernameList();
        List<User> userList = list(new LambdaQueryWrapper<User>().in(User::getUsername, usernameList));
        for (User user : userList) {
            user.setIsDeleted(StatusConstant.ENABLE);
            stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_KEY + RedisConstant.DELETE_KEY + user.getId(), StatusConstant.ENABLE.toString());
            updateById(user);
        }
        return Result.success();
    }

    @Override
    public Result isEnabled(String username, Integer status) {
        if (username==null || status==null){
            throw new PasswordErrorException(UserConstant.USERNAME_AND_STATUS_NOT_FOUND);
        }
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsDeleted, StatusConstant.DISABLE));
        if (user==null){
            throw new UserException(UserConstant.USER_NOT_FOUND);
        }
        user.setIsEnabled(status);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
        // 更新缓存
        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_KEY + RedisConstant.STATUS_KEY + user.getId(), status.toString());
        return Result.success();
    }

    @Override
    public Result resetPassword(String username) {
        if (username == null){
            throw new PasswordErrorException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsDeleted, StatusConstant.DISABLE));
        if (user==null){
            throw new UserException(UserConstant.USER_NOT_FOUND);
        }
        String newPassword = DigestUtils.md5DigestAsHex(UserConstant.DEFAULT_PASSWORD.getBytes());
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
        return Result.success();
    }

    @Override
    public Result changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword==null || newPassword==null){
            throw new PasswordErrorException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsDeleted, StatusConstant.DISABLE));
        if (user==null){
            throw new UserException(UserConstant.USER_NOT_FOUND);
        }
        if (!DigestUtils.md5DigestAsHex(oldPassword.getBytes()).equals(user.getPassword())){
            throw new UserException(UserConstant.OLD_PASSWORD_IS_WRONG);
        }
        String password = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        user.setPassword(password);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
        return Result.success();
    }
}

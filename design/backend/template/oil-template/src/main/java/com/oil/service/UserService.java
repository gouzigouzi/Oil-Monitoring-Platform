package com.oil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oil.dto.UserDTO;
import com.oil.dto.UserLoginDTO;
import com.oil.dto.UserPageQueryDTO;
import com.oil.entity.User;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.vo.UserInfoVo;
import com.oil.vo.UserLoginVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * ClassName: UserService
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/25
 * @Version 1.0
 */
public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    Result<UserLoginVO> login(UserLoginDTO userLoginDTO);

    /**
     * 通过token进行用户验证
     * @param token
     * @return
     */
    Result<UserInfoVo> info(String token);

    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    Result<PageResult> page(UserPageQueryDTO userPageQueryDTO);

    /**
     * 获取用户的公司名称
     * @return
     */
    Result<Set<String>> company();

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    @Transactional
    Result addUser(UserDTO userDTO);

    /**
     * 删除用户
     * @param usernameList
     */
    @Transactional
    Result deleteByUsernameList(List<String> usernameList);

    /**
     * 改变用户的状态
     * @param username
     * @param status
     * @return
     */
    @Transactional
    Result isEnabled(String username, Integer status);

    /**
     * 重置用户的密码
     * @param username
     * @return
     */
    Result resetPassword(String username);

    /**
     * 更改用户密码
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Result changePassword(String username, String oldPassword, String newPassword);
}

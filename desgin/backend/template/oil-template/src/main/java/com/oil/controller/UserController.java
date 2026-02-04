package com.oil.controller;

import com.oil.dto.UserDTO;
import com.oil.dto.UserLoginDTO;
import com.oil.dto.UserPageQueryDTO;
import com.oil.properties.JwtProperties;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.UserService;
import com.oil.utils.UserHolder;
import com.oil.vo.UserInfoVo;
import com.oil.vo.UserLoginVO;
import com.oil.vo.UserPageQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * ClassName: UserController
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/25
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登录：{}", userLoginDTO);
        return userService.login(userLoginDTO);
    }

    @GetMapping("info")
    public Result<UserInfoVo> info(@RequestParam(value = "token") String token){
        log.info("用户验证token：{}", token);
        return userService.info(token);
    }

    @GetMapping("company")
    public Result<Set<String>> company(){
        return userService.company();
    }

    @PostMapping("add")
    public Result add(@RequestBody UserDTO userDTO){
        log.info("新增用户：{}", userDTO);
        return userService.addUser(userDTO);
    }

    @DeleteMapping("delete")
    public Result delete(@RequestBody List<String> usernameList){
        log.info("删除的用户名为：{}", usernameList);
        return userService.deleteByUsernameList(usernameList);
    }

    @GetMapping("page")
    public Result<PageResult> page(UserPageQueryDTO userPageQueryDTO){
        log.info("用户分页查询：{}", userPageQueryDTO);
        return userService.page(userPageQueryDTO);
    }

    @PostMapping("enable/{status}")
    public Result isEnabled(@PathVariable Integer status, String username){
        log.info("用户：{}, 状态:{}", username, status);
        return userService.isEnabled(username, status);
    }

    @PostMapping("reset")
    public Result resetPassword(String username){
        log.info("用户:{}重置密码", username);
        return userService.resetPassword(username);
    }

    @PostMapping("change")
    public Result changePassword(String username, String oldPassword, String newPassword){
        log.info("用户:{}更改密码", username);
        return userService.changePassword(username, oldPassword, newPassword);
    }

    @PostMapping("logout")
    public Result logout(){
        log.info("用户退出,id为{}", UserHolder.getUser());
        return Result.success();
    }
}

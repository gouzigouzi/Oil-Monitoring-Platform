package com.oil.interceptor;

import cn.hutool.core.util.StrUtil;
import com.oil.constant.JwtClaimsConstant;
import com.oil.constant.RedisConstant;
import com.oil.constant.StatusConstant;
import com.oil.constant.UserConstant;
import com.oil.mapper.postgres.UserMapper;
import com.oil.properties.JwtProperties;
import com.oil.utils.JwtUtil;
import com.oil.utils.UserHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;


/**
 * ClassName: LoginInterceptor
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2023/12/13
 * @Version 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 基于Redis来进行登录校验
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、获取token
        String token = request.getHeader(jwtProperties.getUserTokenName());
        if(StrUtil.isBlank(token)){
            response.setStatus(StatusConstant.ILLEGAL_TOKEN);
            return false;
        }
        // 2、从token中解析出用户的id
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            // 3、判断这个token是否已经失效
            if (claims.getExpiration().before(new Date())){
                response.setStatus(StatusConstant.TOKEN_EXPIRED);
                return false;
            }
            // 4、判断这个用户是否已经被删除或者被禁用
            // TODO:功能改进：实现单设备登录功能
            Integer id = claims.get(JwtClaimsConstant.USER_ID, Integer.class);
            String status = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_KEY + RedisConstant.STATUS_KEY + id);
            String delete = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_KEY + RedisConstant.DELETE_KEY + id);
            if (status == null || delete == null){
                response.setStatus(StatusConstant.ILLEGAL_USER);
                return false;
            }
//            User user = userMapper.selectById(id);
            if (UserConstant.DISABLED.equals(Integer.parseInt(status)) || StatusConstant.ENABLE.equals(Integer.parseInt(delete))){
                response.setStatus(StatusConstant.ILLEGAL_USER);
                return false;
            }
            UserHolder.saveUser(id);
        }catch (ExpiredJwtException expiredJwtException){
            response.setStatus(StatusConstant.TOKEN_EXPIRED);
            return false;
        }catch (Exception e){
            response.setStatus(StatusConstant.ILLEGAL_TOKEN);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }

}

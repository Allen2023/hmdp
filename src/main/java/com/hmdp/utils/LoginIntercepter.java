package com.hmdp.utils;

import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Auther: Arthur
 * @Date: 2022/11/16 - 11 - 16 - 22:15
 * @Description: com.hmdp.utils
 * @version: 1.0
 */
public class LoginIntercepter implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取session
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        if (user == null) {
            //判断用户是否存在 不存在返回401未授权状态码
            response.setStatus(401);
            return false;
        }
        //保存用户信息到ThreadLocal
        UserHolder.saveUser((User) user);
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       //移除用户
        UserHolder.removeUser();
    }
}

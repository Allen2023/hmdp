package com.hmdp.service.impl;

import java.time.LocalDateTime;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.entity.UserInfo;
import com.hmdp.mapper.UserInfoMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RegexPatterns;
import com.hmdp.utils.RegexUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-24
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    UserMapper userMapper;

    @Override
    public Result sendCode(String phone, HttpSession session) {

        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }
        String code = RandomUtil.randomNumbers(6);

        session.setAttribute("code", code);

        log.debug("发送短信验证码成功,验证码:{}" + code);

        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(loginForm.getPhone())) {
            return Result.fail("手机号格式错误!");
        }
        //2.校验验证码
        String cachecode = (String) session.getAttribute("code");
        if (cachecode.isEmpty() || !cachecode.equals(loginForm.getCode()) ) {
            //3.不一致,报错
            return Result.fail("验证码错误!");
        }
        //4.一致,根据手机号查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, loginForm.getPhone()));
        //5.判断该用户是否注册 没注册给他注册
        if (user == null) {
            //注册新用户
            user = createUserWithPhone(loginForm);
        }
        session.setAttribute("user",user);
        return Result.ok();

    }

    private User createUserWithPhone(LoginFormDTO loginForm) {
        User userSignIn = new User();
        userSignIn.setPhone(loginForm.getPhone());
        userSignIn.setPassword(loginForm.getPassword());
        userSignIn.setNickName(USER_NICK_NAME_PREFIX+RandomUtil.randomString(10));
        userSignIn.setCreateTime(LocalDateTime.now());
        userSignIn.setUpdateTime(LocalDateTime.now());
        userMapper.insert(userSignIn);
        return userSignIn;
    }
}

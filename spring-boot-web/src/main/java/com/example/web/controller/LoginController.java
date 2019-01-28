package com.example.web.controller;

import com.example.common.util.JWTUtil;
import com.example.common.util.MD5Util;
import com.example.common.validation.LoginGroup;
import com.example.service.exception.ServiceException;
import com.example.web.entity.ResultBean;
import com.example.service.user.UserDO;
import com.example.service.user.UserDOService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by L.C.Y on 2018-11-28
 */

@RestController
@RequestMapping
public class LoginController {

    private final
    UserDOService userDOService;

    @Autowired
    public LoginController(UserDOService userDOService) {
        this.userDOService = userDOService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResultBean login(@RequestBody @Validated(LoginGroup.class) UserDO record) throws Exception {
        UserDO userDO =  userDOService.getUserDOByUsername(record.getUsername());
        if (userDO == null) {
            return new ResultBean().fail("登录代码不存在！");
        }

        // 判断用户名大小写一致
        if (!record.getUsername().equals(userDO.getUsername())) {
            return new ResultBean().fail("登录代码或密码不正确！");
        }

        if (!MD5Util.create(record.getPassword()).equals(userDO.getPassword())) {
            return new ResultBean().fail("登录代码或密码不正确！");
        }

        // 判断是否为正常用户
        if ("0".equals(userDO.getStatus())){
            return new ResultBean().fail("用户处于停用状况，不可登录！");
        }

        // 生成token
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userDO.getId());
        String token = JWTUtil.create(payload);

        // shiro认证
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(token, token);
        subject.login(usernamePasswordToken);

        UserDO userDO1 = userDOService.getUserDO(userDO.getId());

        // 最近登录时间
        UserDO user = new UserDO();
        user.setId(userDO.getId()).setLastLoginAt(System.currentTimeMillis());
        userDOService.updateFieldById(user);

        return new ResultBean().success(userDO1.setTaskDate(userDOService.getTaskDate())).withMore("Authorization", token);
    }


}

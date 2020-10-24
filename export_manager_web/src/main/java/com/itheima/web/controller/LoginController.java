package com.itheima.web.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginController extends BaseController {
    @Reference
    private UserService userService;

    /*@RequestMapping("/login")
    public String login(String email, String password) {

        //1. 根据email调用service查询用户
        User user = userService.findByEmail(email);

        //2. 用户为空,说明用户不存在,提示错误,退出
        if (user == null) {
            request.setAttribute("error", "当前用户不存在");
            return "forward:/login.jsp";//强制不去拼接前后缀
        }

        //3. 判断密码, 如果密码比对失败,说明密码错误, 提示错误,退出
        String pass = new Md5Hash(password, email, 2).toString();
        if (!StringUtils.equals(pass, user.getPassword())) {
            request.setAttribute("error", "密码错误");
            return "forward:/login.jsp";//强制不去拼接前后缀
        }

        //4. 登录成功了
        //向session中存储用户信息
        session.setAttribute("loginUser", user);

        //根据用户查询对应的权限
        List<Module> moduleList = userService.findModuleByUser(user);
        session.setAttribute("modules", moduleList);

        //跳转主页面
        return "redirect:/home/main.do";
    }*/


    @RequestMapping("/login")
    public String login(String email, String password) {

        //1. 封装email和password为Token
        AuthenticationToken authenticationToken = new UsernamePasswordToken(email, new Md5Hash(password, email, 2).toString());

        //2. 获取Subject,并且调用login方法
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(authenticationToken);

            //3. 登录成功
            User user = (User) subject.getPrincipal();

            session.setAttribute("loginUser", user);

            List<Module> moduleList = userService.findModuleByUser(user);
            session.setAttribute("modules", moduleList);
            return "redirect:/home/main.do";

        } catch (Exception e) {
            request.setAttribute("error", "用户名或者密码错误");
            return "forward:/login.jsp";
        }
    }

    @RequestMapping("/home/main")
    public String main() {
        return "home/main";
    }

    @RequestMapping("/home/home")
    public String home() {
        return "home/home";
    }

    //退出
    @RequestMapping(value = "/logout", name = "用户登出")
    public String logout() {
        SecurityUtils.getSubject().logout();   //登出
        return "redirect:/login.jsp";
    }
}

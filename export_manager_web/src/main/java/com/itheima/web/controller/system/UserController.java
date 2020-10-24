package com.itheima.web.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Dept;
import com.itheima.domain.system.Role;
import com.itheima.domain.system.User;
import com.itheima.service.system.DeptService;
import com.itheima.service.system.RoleService;
import com.itheima.service.system.UserService;
import com.itheima.utils.MailUtil;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {

    @Reference
    private DeptService deptService;
    @Reference
    private UserService userService;
    @Reference
    private RoleService roleService;

    @Autowired
    private AmqpTemplate template;

    @RequestMapping(value = "/list", name = "用户列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo pageInfo = userService.findByPage(getCompanyId(), pageNum, pageSize);
        request.setAttribute("page", pageInfo);

        return "/system/user/user-list";
    }


    @RequestMapping(value = "/toAdd", name = "跳转用户新增页面")
    public String toAdd() {
        //1. 查询所有部门
        List<Dept> deptList = deptService.findAll(getCompanyId());
        request.setAttribute("deptList", deptList);

        return "/system/user/user-add";
    }


    @RequestMapping(value = "/toUpdate", name = "跳转用户编辑页面")
    public String toUpdate(String id) {
        //1. 根据id查询当前用户信息
        User user = userService.findById(id);

        //设置密码为null
        user.setPassword(null);
        request.setAttribute("user", user);

        //2. 查询所有部门
        List<Dept> deptList = deptService.findAll(getCompanyId());
        request.setAttribute("deptList", deptList);

        //3. 转发到修改页面
        return "/system/user/user-update";
    }

    @RequestMapping(value = "/edit", name = "用户新增或编辑")
    public String edit(User user) {
        //如果传入的密码不是空,加密
        String oldPassword = user.getPassword();
        if (StringUtils.isNotEmpty(oldPassword)) {
            String newPassword = new Md5Hash(oldPassword, user.getEmail(), 2).toString();
            user.setPassword(newPassword);
        }

        if (StringUtils.isEmpty(user.getId())) {
            //1. 设置主键
            user.setId(UUID.randomUUID().toString());

            //2. 设置企业信息
            user.setCompanyId(getCompanyId());
            user.setCompanyName(getCompanyName());

            //3 用户保存成功
            userService.save(user);

            //4 开始发送邮件
//            MailUtil.sendMail(to, title, content);
            String to = user.getEmail();
            String title = "saas平台--用户新增成功";
            String content = "恭喜您,您的账号已经在saas平台开通成功,请使用当前邮箱作用账号,使用" + oldPassword + "作为密码进行登录";

            //4. 向MQ中发送消息
            Map map = new HashMap<>();
            map.put("to", to);
            map.put("title", title);
            map.put("content", content);

            template.convertAndSend("mail.send", map);
        } else {
            userService.update(user);
        }

        //重定向到list方法
        return "redirect:/system/user/list.do";
    }

    @RequestMapping(value = "/delete", name = "用户删除")
    public String delete(String id) {
        //调用service删除
        userService.deleteById(id);

        //重定向到list方法
        return "redirect:/system/user/list.do";
    }


    @RequestMapping(value = "/roleList", name = "跳转用户分配角色页面")
    public String roleList(String id) {
        //1. 显示出用户名称(查询用户信息)
        User user = userService.findById(id);
        request.setAttribute("user", user);

        //2. 显示出所有的角色, 等待勾选( 查询所有角色)
        List<Role> roleList = roleService.findAll(getCompanyId());
        request.setAttribute("roleList", roleList);

        //3. 回显当前用户已经分配了的角色 ( 查询中间表 )
        List<String> userRoleStr = userService.findRoleIdsByUserId(id);
        request.setAttribute("userRoleStr", userRoleStr);

        //4. 跳转到给用户分配角色页面
        return "/system/user/user-role";
    }


    @RequestMapping(value = "/changeRole", name = "用户分配角色")
    public String changeRole(@RequestParam("userid") String userId, String[] roleIds) {
        //1. 调用service进行用户角色的分配
        userService.changeRole(userId, roleIds);

        //2. 重定向到list方法
        return "redirect:/system/user/list.do";
    }
}

package com.itheima.web.realm;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.service.system.UserService;
import com.itheima.web.utils.SpringUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

public class SaasRealm extends AuthorizingRealm {

//    @Reference
//    private UserService userService;


    //认证数据准备
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("==========================================认证===============================================");

        //获取email
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String email = usernamePasswordToken.getUsername();

        //查询用户信息
        UserService userService = (UserService) SpringUtil.getBean("userService");
        User user = userService.findByEmail(email);

        //返回用户信息
        if (user == null) {
            return new SimpleAuthenticationInfo();
        } else {
            // Object principal 主角
            // Object credentials  密码
            // String realmName 当前realm的名称
            return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
        }
    }

    //授权数据准备
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("==========================================授权===============================================");

        //查询当前用户的权限信息
        User user = (User) principalCollection.getPrimaryPrincipal();

        UserService userService = (UserService) SpringUtil.getBean("userService");
        List<Module> moduleList = userService.findModuleByUser(user);

        //将信息交给shiro
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (Module module : moduleList) {
            simpleAuthorizationInfo.addStringPermission(module.getName());
        }

        System.out.println(simpleAuthorizationInfo.getStringPermissions());
        return simpleAuthorizationInfo;
    }
}

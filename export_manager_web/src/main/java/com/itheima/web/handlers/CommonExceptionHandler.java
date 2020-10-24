package com.itheima.web.handlers;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CommonExceptionHandler {

    //处理所有的Exception及其子类
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, HttpServletRequest request) {

        //1. 打印异常,给程序员
        e.printStackTrace();
        request.setAttribute("errorMsg", e.getMessage());

        //2. 返回页面, 给用户
        return "error";
    }


    //处理未授权的异常
    @ExceptionHandler(UnauthorizedException.class)
    public String UnauthorizedException(Exception e, HttpServletRequest request) {
        //2. 返回未授权页面, 给用户
        return "redirect:/unauthorized.jsp";
    }
}

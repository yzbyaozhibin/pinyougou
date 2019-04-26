package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-01<p>
 */
@Controller
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    /** 登录方法 */
    @RequestMapping("/login")
    public String login(String username, String password, String code,
                        HttpServletRequest request){
        try{
            System.out.println("username = " + username);
            System.out.println("password = " + password);
            System.out.println("code = " + code);
            System.out.println("请求方式：" + request.getMethod());
            // 判断请求方式
            if ("post".equalsIgnoreCase(request.getMethod())){
                // 1. 判断验证码
                // 1.1 从Session中获取验证码
                String oldCode = (String)request.getSession()
                        .getAttribute(VerifyController.VERIFY_CODE);
                System.out.println("oldCode = " + oldCode);
                if (code.equalsIgnoreCase(oldCode)){
                    // 2. 用户登录或角色还是由SpringSecurity
                    // 2. 1 创建用户名与密码凭证
                    UsernamePasswordAuthenticationToken token = new
                            UsernamePasswordAuthenticationToken(username, password);
                    // 2.2 认证
                    Authentication authenticate = authenticationManager.authenticate(token);

                    // 2.3 判断是否认证成功
                    if (authenticate.isAuthenticated()){
                        // 2.4 安全上下文设置认证对象
                        SecurityContextHolder.getContext().setAuthentication(authenticate);

                        return "redirect:/admin/index.html";
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "redirect:/shoplogin.html";
    }


    /** 获取登录用户名 */
    @GetMapping("/showLoginName")
    @ResponseBody
    public Map<String,String> showLoginName(){
        // 获取安全上下文对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取用户名
        String loginName = context.getAuthentication().getName();
        System.out.println("loginName = " + loginName);

        Map<String,String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }
}

package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-15<p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    /** 用户注册 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String code){
        try{
            // 检验验证码是否正确
            boolean flag = userService.checkSmsCode(user.getPhone(), code);
            if (flag) {
                userService.save(user);
            }
            return flag;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    /** 发送短信验证码 */
    @GetMapping("/sendSmsCode")
    public boolean sendSmsCode(String phone){
        try{
            return userService.sendSmsCode(phone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    //验证原密码
    @PostMapping("/codePassword")
    public Boolean codePassword(@RequestBody Map userMap,HttpServletRequest request){
        String oldPassword = (String) userMap.get("oldPassword");
        String username = request.getRemoteUser();
        User user = userService.selectUser(username);
        String password = user.getPassword();
        if (DigestUtils.md5Hex(oldPassword).equals(password)){
            userService.updatePassword((String)userMap.get("newPassword"),username);
            return true;
        }else {
            return false;
        }
    }
}

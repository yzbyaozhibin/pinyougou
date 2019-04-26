package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

/**
 * 商家控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-01<p>
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 商家申请入驻 */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller){
        try{
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    //显示商家资料
    @GetMapping("/findOne")
    public Seller findOne(){
         try {
             // 获取登录用户名
             String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
             return sellerService.findOne(sellerId);

         }catch (Exception ex){
             ex.printStackTrace();
         }
        return null;
    }


    //商家资料修改
    @PostMapping("/update")
    public boolean update(@RequestBody Seller seller){
         try {
             sellerService.update(seller);
             return true;
             }catch (Exception ex){
             ex.printStackTrace();
             }
        return false;
    }

    //商家修改密码
    @PostMapping("/changePassword")
    public Boolean changePassword(@RequestBody Map<String,Object> map){
        try {
            //获取登录商家用户名
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            Seller seller = sellerService.findOne(sellerId);
            String password = seller.getPassword();
            String oldPassword = (String) map.get("oldPassword");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(oldPassword ,password)){
                sellerService.updatePassword(encoder.encode((String) map.get("newPassword")),sellerId);
                return true;
            }
            }catch (Exception ex){
            ex.printStackTrace();
            }
            return false;
    }

}

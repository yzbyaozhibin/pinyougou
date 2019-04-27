package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.*;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import com.pinyougou.service.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
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

    @Reference(timeout = 10000)
    private ProvincesService provincesService;
    @Reference(timeout = 10000)
    private CitiesService citiesService;
    @Reference(timeout = 10000)
    private AreasService areasService;

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

    @GetMapping("/findUserByUsername")
    public Map<String,Object> findUserByUsername(String username){
        Map<String,Object > map = new HashMap<>();
        try{
            User user = userService.findUserByUsername(username);
            map.put("user",user);
            map.put("birthdayString",new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthday() == null ? new Date():user.getBirthday()));
            return map;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    //修改的方法
    @PostMapping("/updateAddress")
    public Boolean updateAddress(@RequestBody Address address){
        try {
            userService.updateAddress(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //保存的方法

    @PostMapping("/saveAddress")
    public Boolean saveAddress(@RequestBody Address address,HttpServletRequest request){
        try {
            address.setUserId(request.getRemoteUser());
            userService.saveAddress(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @GetMapping("/findProvincesList")
    public List<Provinces> findProvincesList(){
        try{
            return provincesService.findAll();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    @GetMapping("/deleteAddress")
    public Boolean deleteAddress(Long id){
        try {
            userService.deleteAddress(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @GetMapping("/findCitiesList")
    public List<Cities> findCitiesList(String provinceId){
        try{
            System.out.println(provinceId);
            return citiesService.findByProvinceId(provinceId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @GetMapping("/findAreasList")
    public List<Areas> findAreasList(String cityId){
        try{
            return areasService.findByCityId(cityId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @PostMapping("/saveUser")
    public Boolean saveUser(@RequestBody User entity){
        try{
            userService.saveUser(entity);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    //修改密码
    @PostMapping("/codePassword")
    public Boolean codePassword(@RequestBody Map userMap,HttpServletRequest request){
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/findAddressByParentId")
    public List<Provinces> findAllProvince(){
        try {
            return userService.findAllProvince();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/findCitiesByProvinceId")
    public List<Cities> findCitiesByProvinceId(String provinceId){
        try {
            return userService.findAllCitiesByProvinceId(provinceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/findAreasByCityId")
    public List<Areas> findAreasByCityId(String cityId){
        try {
            return userService.findAreasByCityId(cityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/findUserPhoneByUserId")
    public String findUserPhoneByUserId(HttpServletRequest request){
        try {
            return userService.findUserPhoneByUserId(request.getRemoteUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/judgeCode")
    public Boolean judgeCode(String code,HttpServletRequest request){
        try {
            String ve = (String) request.getSession().getAttribute(VerifyController.VERIFY_CODE);
            if (ve.equals(code)){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/sendCode")
    public Boolean sendCode(String phone){
        try {
            userService.sendCode(phone);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/checkSmsCode")
    public Boolean checkSmsCode(String phone,String code,HttpServletRequest request){
        try {
           if(userService.checkSmsCode(phone, code)){
               userService.saveVerifyPhone(request.getRemoteUser(),"true");
                return true;
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @GetMapping("/updatePhone")
    public Map<String, Object> updatePhone(String phone,String code,HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
            if (!userService.verifyPhone(request.getRemoteUser(), "true")){
                map.put("success",false);
                map.put("message","请先验证手机号");
                return map;
            }
            if(userService.checkSmsCode(phone, code)){
                User user = new User();
                user.setPhone(phone);
                user.setUsername(request.getRemoteUser());
                user.setUpdated(new Date());
                userService.updatePhone(user);
                map.put("success",true);
                return map;
            } else {
                map.put("success",false);
                map.put("message","短信验证码不正确");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("success",false);
        map.put("message","网络出现异常");
        return map;
    }



    //修改的方法
    @PostMapping("/updateAddress")
    public Boolean updateAddress(@RequestBody Address address){
        try {
            userService.updateAddress(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //保存的方法

    @PostMapping("/saveAddress")
    public Boolean saveAddress(@RequestBody Address address,HttpServletRequest request){
        try {
            address.setUserId(request.getRemoteUser());
            userService.saveAddress(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/deleteAddress")
    public Boolean deleteAddress(Long id){
        try {
            userService.deleteAddress(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

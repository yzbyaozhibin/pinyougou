package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Areas;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import com.pinyougou.service.*;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
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

            map.put("birthdayString",new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthday()));
            return map;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
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

}

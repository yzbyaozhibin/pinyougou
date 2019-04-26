package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 地址控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-20<p>
 */
@RestController
@RequestMapping("/order")
public class AddressController {

    @Reference(timeout = 10000)
    private AddressService addressService;

    /** 根据登录用户名获取收件地址列表 */
    @GetMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request){
        // 获取登录用户名
        String userId = request.getRemoteUser();
        return addressService.findAddressByUser(userId);
    }
    /** 保存收件人信息 */
    @PostMapping("/saveAddress")
    public boolean save(HttpServletRequest request ,@RequestBody Address address){
        try {
            // 获取登录用户名
            String userId = request.getRemoteUser();
            address.setUserId(userId);
            address.setIsDefault("0");
            addressService.save(address);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /** 修改收件人信息 */
    @PostMapping("/updateAddress")
    public boolean update(@RequestBody Address address){
        try {
            addressService.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /** 删除收件人信息 */
    @GetMapping("/deleteAddress")
    public boolean update(Long id){
        try {
            addressService.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

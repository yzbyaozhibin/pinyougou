package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/address")
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

}

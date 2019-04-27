package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(timeout = 10000)
    private OrderService orderService;

    @GetMapping("/findOrders")
    public List<Map<String,Object>> findOrders(Long num,Long size,HttpServletRequest request){
        return orderService.findAllOrderByUserId(request.getRemoteUser());
    }

    @PostMapping("/pay")
    public Boolean pay(@RequestBody Order order,String totalFee){
        try {
            orderService.userSave(order,Double.valueOf(totalFee));
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
}

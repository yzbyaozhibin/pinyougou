package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 秒杀订单控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-22<p>
 */
@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /** 秒杀下单 */
    @GetMapping("/submitOrder")
    public boolean submitOrder(HttpServletRequest request, Long id){
        try {
            // 获取登录用户名
            String userId = request.getRemoteUser();
            // 调用秒杀订单服务接口
            seckillOrderService.submitOrderRedis(userId, id);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 生成微信支付 */
    @GetMapping("/genPayCode")
    public Map<String, Object> genPayCode(HttpServletRequest request){
        // 获取登录用户名
        String userId = request.getRemoteUser();
        // 从Redis数据库中获取秒杀订单
        SeckillOrder seckillOrder = seckillOrderService
                .findSeckillOrderFromRedis(userId);
        // 支付金额
        long money = (long)(seckillOrder.getMoney().doubleValue() * 100);
        return weixinPayService.genPayCode(seckillOrder.getId().toString(), money + "");
    }

    /** 检测支付状态 */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo, HttpServletRequest request){
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            // 调用微信支付服务接口
            Map<String,String> resMap = weixinPayService.queryPayStatus(outTradeNo);

            if (resMap != null){
                // SUCCESS-支付成功
                if ("SUCCESS".equals(resMap.get("trade_state"))){

                    // 获取登录用户名
                    String userId = request.getRemoteUser();

                    // 支付成功，保存秒杀订单
                    seckillOrderService.saveSeckillOrder(userId, resMap.get("transaction_id"));

                    data.put("status", 1);
                }
                // NOTPAY—未支付
                if ("NOTPAY".equals(resMap.get("trade_state"))){
                    data.put("status", 2);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}

package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀订单任务调度类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-24<p>
 */
@Component
public class SeckillOrderTask {

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 查询秒杀订单超时5分钟未支付，需要关闭订单
     * 定时任务的方法(间隔3秒)
     * cron : 触发任务的时间表达式
     * 格式: 秒  分  小时  日  月 周
     * */
    @Scheduled(cron = "0/3 * * * * ?")
    public void closeOrderTask(){
        System.out.println("当前时间:" + new Date());

        // 1. 查询超时5分钟未支付的秒杀订单
        List<SeckillOrder> seckillOrderList = seckillOrderService.findOrderByTimeout();
        System.out.println("超时5分钟未支付的秒杀订单的数量：" + seckillOrderList.size());

        // 2. 调用微信支付系统的关单接口
        for (SeckillOrder seckillOrder : seckillOrderList) {
            // 调用微信支付服务接口
            Map<String,String> resMap = weixinPayService
                    .closePayTimeout(seckillOrder.getId().toString());
            // 判断关闭订单是否成功
            if (resMap != null && "SUCCESS".equals(resMap.get("return_code"))){
                // 3. 删除未支付的秒杀订单，增加库存
                seckillOrderService.deleteOrderFromRedis(seckillOrder);
            }
        }

    }

}

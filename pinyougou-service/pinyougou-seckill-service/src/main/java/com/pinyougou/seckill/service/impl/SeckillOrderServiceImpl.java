package com.pinyougou.seckill.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 秒杀订单服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-22<p>
 */
@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    /**
     * 秒杀下单
     * synchronized: 线程锁 (单进程，多线程)
     * 多进程，多线程: 分布式锁(Redis、关系型数据库、zookeeper)
     * */
    public synchronized void submitOrderRedis(String userId, Long id){
        try{
            // 1. 从Redis数据库获取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate
                    .boundHashOps("seckillGoodsList").get(id);

            // 判断秒杀商品
            if (seckillGoods != null && seckillGoods.getStockCount() > 0){
                // 2. 扣减库存(线程安全)
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 2.1 判断剩余库存
                if (seckillGoods.getStockCount() == 0){ // 秒光了
                    // 2.2 同步秒杀商品到数据库
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    // 2.3  从Redis数据库删除秒杀商品
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                }else{ // 没有秒光
                    // 把秒杀商品重新存入Redis
                    redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                }

                // 3. 产生秒杀订单
                SeckillOrder seckillOrder = new SeckillOrder();
                // 秒杀订单id
                seckillOrder.setId(idWorker.nextId());
                // 秒杀商品id
                seckillOrder.setSeckillId(seckillGoods.getId());
                // 秒杀商品金额
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 秒杀用户的id
                seckillOrder.setUserId(userId);
                // 秒杀商品的商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 秒杀订单创建时间
                seckillOrder.setCreateTime(new Date());
                // 秒杀订单支付状态
                seckillOrder.setStatus("0");

                // 4. 把秒杀订单存储到Redis数据库
                redisTemplate.boundHashOps("seckillOrderList").put(userId, seckillOrder);
            }

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 从Redis数据库中获取秒杀订单 */
    public SeckillOrder findSeckillOrderFromRedis(String userId){
        try{
            return (SeckillOrder)redisTemplate.
                    boundHashOps("seckillOrderList").get(userId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 保存秒杀订单 */
    public void saveSeckillOrder(String userId, String transactionId){
        try{
            //  从Redis数据库中获取秒杀订单
            SeckillOrder seckillOrder = findSeckillOrderFromRedis(userId);
            seckillOrder.setPayTime(new Date());
            seckillOrder.setStatus("1");
            seckillOrder.setTransactionId(transactionId);

            //  同步到数据库
            seckillOrderMapper.insertSelective(seckillOrder);

            // 从Redis数据库中删除秒杀订单
            redisTemplate.boundHashOps("seckillOrderList").delete(userId);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 查询超时5分钟未支付的秒杀订单 */
    public List<SeckillOrder> findOrderByTimeout(){
        try{
            List<SeckillOrder> seckillOrders = new ArrayList<>();
            // 1. 从Redis数据库中查询全部未支付的秒杀订单
            List<SeckillOrder> seckillOrderList = redisTemplate
                    .boundHashOps("seckillOrderList").values();
            // 2. 迭代所有未支付的秒杀订单
            for (SeckillOrder seckillOrder : seckillOrderList) {
                // 3. 判断哪些未支付的秒杀订单超出了5分钟(秒杀订单的创建时间)
                // 当前时间的毫秒数 - 5分钟的毫秒数
                long date  = new Date().getTime() - 5 * 60 * 1000;
                if (seckillOrder.getCreateTime().getTime() < date){
                    // 把超时的秒杀订单放到新的集合中
                    seckillOrders.add(seckillOrder);
                }
            }
            return seckillOrders;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 删除未支付的秒杀订单，增加库存 */
    public void deleteOrderFromRedis(SeckillOrder seckillOrder){
        try{
            // 1. 从Redis中删除已关闭的秒杀订单
            redisTemplate.boundHashOps("seckillOrderList")
                    .delete(seckillOrder.getUserId());

            // 2. 增加秒杀商品的库存
            // 2.1 从Redis数据库中获取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate
                    .boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId());
            if (seckillGoods == null){ // 秒光了
                // 从数据库中查询
                seckillGoods = seckillGoodsMapper
                        .selectByPrimaryKey(seckillOrder.getSeckillId());
                seckillGoods.setStockCount(1);
            }else{
                // 增加库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            }
            // 3. 把秒杀商品存储Redis
            redisTemplate.boundHashOps("seckillGoodsList")
                    .put(seckillGoods.getId(), seckillGoods);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

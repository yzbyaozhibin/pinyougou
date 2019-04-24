package com.pinyougou.service;

import com.pinyougou.pojo.SeckillOrder;
import java.util.List;
import java.io.Serializable;
/**
 * SeckillOrderService 服务接口
 * @date 2019-03-28 09:58:00
 * @version 1.0
 */
public interface SeckillOrderService {

	/** 添加方法 */
	void save(SeckillOrder seckillOrder);

	/** 修改方法 */
	void update(SeckillOrder seckillOrder);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	SeckillOrder findOne(Serializable id);

	/** 查询全部 */
	List<SeckillOrder> findAll();

	/** 多条件分页查询 */
	List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows);

	/** 秒杀下单 */
    void submitOrderRedis(String userId, Long id);

    /** 从Redis数据库中获取秒杀订单 */
    SeckillOrder findSeckillOrderFromRedis(String userId);

    /** 保存秒杀订单 */
	void saveSeckillOrder(String userId, String transactionId);

	/** 查询超时5分钟未支付的秒杀订单 */
    List<SeckillOrder> findOrderByTimeout();

    /** 删除未支付的秒杀订单，增加库存 */
	void deleteOrderFromRedis(SeckillOrder seckillOrder);
}
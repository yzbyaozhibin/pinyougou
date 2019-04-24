package com.pinyougou.service;

import com.pinyougou.pojo.SeckillGoods;
import java.util.List;
import java.io.Serializable;
/**
 * SeckillGoodsService 服务接口
 * @date 2019-03-28 09:58:00
 * @version 1.0
 */
public interface SeckillGoodsService {

	/** 添加方法 */
	void save(SeckillGoods seckillGoods);

	/** 修改方法 */
	void update(SeckillGoods seckillGoods);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	SeckillGoods findOne(Serializable id);

	/** 查询全部 */
	List<SeckillGoods> findAll();

	/** 多条件分页查询 */
	List<SeckillGoods> findByPage(SeckillGoods seckillGoods, int page, int rows);

	/** 查询正在秒杀的商品 */
    List<SeckillGoods> findSeckillGoods();

    /** 根据秒杀商品id查询秒杀商品对象 */
	SeckillGoods findSeckillGoodsFromRedis(Long id);
}
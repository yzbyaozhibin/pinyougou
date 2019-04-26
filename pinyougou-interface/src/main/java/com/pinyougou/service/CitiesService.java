package com.pinyougou.service;

import com.pinyougou.pojo.Cities;
import java.util.List;
import java.io.Serializable;
/**
 * CitiesService 服务接口
 * @date 2019-03-28 09:58:00
 * @version 1.0
 */
public interface CitiesService {

	/** 添加方法 */
	void save(Cities cities);

	/** 修改方法 */
	void update(Cities cities);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Cities findOne(Serializable id);

	/** 查询全部 */
	List<Cities> findAll();

	/** 多条件分页查询 */
	List<Cities> findByPage(Cities cities, int page, int rows);

	List<Cities> findByProvinceId(String provinceId);

}
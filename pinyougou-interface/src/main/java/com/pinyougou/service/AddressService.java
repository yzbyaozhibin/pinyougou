package com.pinyougou.service;

import com.pinyougou.pojo.Address;
import java.util.List;
import java.io.Serializable;

public interface AddressService {

	/** 添加方法 */
	void save(Address address);

	/** 修改方法 */
	void update(Address address);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Address findOne(Serializable id);

	/** 查询全部 */
	List<Address> findAll();

	/** 多条件分页查询 */
	List<Address> findByPage(Address address, int page, int rows);

	/** 根据登录用户名获取收件地址列表 */
    List<Address> findAddressByUser(String userId);

}
package com.pinyougou.service;

import com.pinyougou.pojo.*;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * UserService 服务接口
 * @date 2019-03-28 09:58:00
 * @version 1.0
 */
public interface UserService {

	/** 添加方法 */
	void save(User user);

	/** 修改方法 */
	void update(User user);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	User findOne(Serializable id);

	/** 查询全部 */
	List<User> findAll();

	/** 多条件分页查询 */
	List<User> findByPage(User user, int page, int rows);

	/** 发送短信验证码 */
	boolean sendSmsCode(String phone);

	/** 检验验证码是否正确 */
	boolean checkSmsCode(String phone, String code);

	User findUserByUsername(String username);

	void saveUser(User user);

	User selectUser(String username);

    void updatePassword(String newPassword,String username);

    //查询所有省份
	List<Provinces> findAllProvince();


	List<Cities> findAllCitiesByProvinceId(String provinceId);

	List<Areas> findAreasByCityId(String cityId);

	//修改地址的方法
    void updateAddress(Address address);

    //保存地址的方法
    void saveAddress(Address address);

    //删除地址的方法
    void deleteAddress(Long id);

//    //修改默认地址为1改成0
//    Boolean updateDefaultAddressOld();
//
//    //修改默认地址为0改成1
//    void updateDefaultAddressNew(String isDefault, Long id);


    String findUserPhoneByUserId(String userId);

    void sendCode(String phone);

    void saveVerifyPhone(String remoteUser, String aTrue);

    Boolean verifyPhone(String remoteUser, String aTrue);

    void updatePhone(User user);

    void updateAddress(Address address);

    void saveAddress(Address address);

	void deleteAddress(Long id);
}
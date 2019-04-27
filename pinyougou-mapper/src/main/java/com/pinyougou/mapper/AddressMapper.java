package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Address;

/**
 * AddressMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface AddressMapper extends Mapper<Address>{

    @Delete("delete from tb_address where id=#{id}")
    void deleteAddress(Long id);
}
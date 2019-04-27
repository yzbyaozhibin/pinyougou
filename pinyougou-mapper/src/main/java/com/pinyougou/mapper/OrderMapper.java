package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Order;

import java.util.List;

/**
 * OrderMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface OrderMapper extends Mapper<Order>{

    @Select("select * from tb_order where user_id =#{userId}")
    List<Order> findAllByUserId(String userId);

}
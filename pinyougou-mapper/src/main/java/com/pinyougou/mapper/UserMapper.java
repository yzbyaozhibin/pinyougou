package com.pinyougou.mapper;

import com.pinyougou.pojo.Provinces;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

import java.util.List;

/**
 * UserMapper 数据访问接口
 * @date 2019-03-28 09:54:28
 * @version 1.0
 */
public interface UserMapper extends Mapper<User>{

    @Update("UPDATE  tb_user set password=#{password} where username=#{username}")
    void updatePasswordByUsername(User user);


    @Select("select * from tb_user where username=#{username}")
    User selectUser(String username);

    @Select("select * from tb_provinces")
    List<Provinces> selectProvinces();

}
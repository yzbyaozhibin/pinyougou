package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-04-15<p>
 */
@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private AreasMapper areasMapper;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsQueue;

    @Override
    public void save(User user) {
        try {
            // 密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            // 创建时间
            user.setCreated(new Date());
            // 修改时间
            user.setUpdated(user.getCreated());
            // 添加数据
            userMapper.insertSelective(user);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void saveUser(User user){
        try{
            User u = new User();
            u.setUsername(user.getUsername());
            User user1 = userMapper.selectOne(u);
            user.setId(user1.getId());
            userMapper.updateByPrimaryKeySelective(user);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    /**
     * 发送短信验证码
     */
    public boolean sendSmsCode(String phone) {
        try {
            // 1. 随机生成6位数字的验证码 95db9eb9-94e8-48e7-a5b2-97c622644e70
            String code = UUID.randomUUID().toString().replaceAll("-", "")
                    .replaceAll("[a-zA-Z]", "").substring(0, 6);
            System.out.println("code= " + code);


            // 2. 调用短信发送接口(HttpClientUtils)
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            // 定义Map集合封装请求参数 18502903967
            Map<String, String> params = new HashMap<>();
            params.put("phone", phone);
            params.put("signName", signName);
            params.put("templateCode", templateCode);
            params.put("templateParam", "{'number' : '" + code + "'}");
            // 发送post请求
            String content = httpClientUtils.sendPost(smsUrl, params);
            System.out.println("content = " + content);

            // 3. 判断短信是否发送成功，如果发送成功，就需要把验证存储到Redis(时间90秒)
            // {success : true}
            Map map = JSON.parseObject(content, Map.class);
            boolean success = (boolean) map.get("success");
            if (success) {
                // 把验证存储到Redis(时间90秒)
                redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            }

            return success;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 检验验证码是否正确
     */
    public boolean checkSmsCode(String phone, String code) {
        try {
            String oldCode = (String) redisTemplate.boundValueOps(phone).get();
            return code.equals(oldCode);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User findUserByUsername(String username) {
        try{
            User user = new User();
            user.setUsername(username);
            return userMapper.selectOne(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User selectUser(String username) {
        return userMapper.selectUser(username);
    }

    @Override
    public void updatePassword(String newPassword, String username) {
        User user = new User();
        user.setPassword(DigestUtils.md5Hex(newPassword));
        user.setUsername(username);
        user.setUpdated(new Date());
        userMapper.updatePasswordByUsername(user);
    }

    @Override
    public List<Provinces> findAllProvince() {
        return userMapper.selectProvinces();
    }

    @Override
    public List<Cities> findAllCitiesByProvinceId(String provinceId) {
        return citiesMapper.findAllCitiesByProvinceId(provinceId);
    }

    @Override
    public List<Areas> findAreasByCityId(String cityId) {
        return areasMapper.findAreasByCityId(cityId);
    }

    @Override
    public String findUserPhoneByUserId(String userId) {
        return userMapper.findUserPhoneByUserId(userId);
    }

    @Override
    public void sendCode(String phone) {
        try {
            // 1. 随机生成6位数字的验证码 95db9eb9-94e8-48e7-a5b2-97c622644e70
            String code = UUID.randomUUID().toString().replaceAll("-", "")
                    .replaceAll("[a-zA-Z]", "").substring(0, 6);
            System.out.println("code= " + code);
            jmsTemplate.send(smsQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    MapMessage mapMessage = session.createMapMessage();
                    mapMessage.setString("phone",phone);
                    mapMessage.setString("signName",signName);
                    mapMessage.setString("templateCode",templateCode);
                    mapMessage.setString("templateParam","{'code' : '" + code + "'}");
                    return mapMessage;
                }

            });
            // 把验证存储到Redis(时间90秒)
            redisTemplate.boundValueOps(phone).set(code, 300, TimeUnit.SECONDS);

        } catch (Exception e) {
        }
    }

    @Override
    public void saveVerifyPhone(String remoteUser, String aTrue) {
        redisTemplate.boundValueOps("VerifyPhone_" + remoteUser).set(aTrue, 300, TimeUnit.SECONDS);
    }

    @Override
    public Boolean verifyPhone(String remoteUser, String aTrue) {
        String b = (String) redisTemplate.boundValueOps("VerifyPhone_" + remoteUser).get();
        return aTrue.equals(b);
    }

    @Override
    public void updatePhone(User user) {
        userMapper.updatePhone(user);
    }
    @Override
    public void updateAddress(Address address) {
        try {
            addressMapper.updateByPrimaryKey(address);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //保存地址的方法
    @Override
    public void saveAddress(Address address) {
        try {
            address.setIsDefault("0");
            address.setCreateDate(new Date());
            addressMapper.insertSelective(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deleteAddress(Long id) {
        addressMapper.deleteAddress(id);
    }
}

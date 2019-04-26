package com.pinyougou.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.pojo.Cities;
import com.pinyougou.service.CitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.CitiesService")
@Transactional
public class CitiesServiceImpl implements CitiesService{
    @Autowired
    private CitiesMapper citiesMapper;
    @Override
    public void save(Cities cities) {

    }

    @Override
    public void update(Cities cities) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Cities findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Cities> findAll() {
        return citiesMapper.selectAll();
    }

    @Override
    public List<Cities> findByPage(Cities cities, int page, int rows) {
        return null;
    }

    @Override
    public List<Cities> findByProvinceId(String provinceId){
        try {
            Cities cities = new Cities();
            cities.setProvinceId(provinceId);
            return citiesMapper.select(cities);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

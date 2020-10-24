package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.FactoryDao;
import com.itheima.domain.cargo.Factory;
import com.itheima.domain.cargo.FactoryExample;
import com.itheima.service.cargo.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class FactoryServiceImpl implements FactoryService {

    //一定注意Dao的所有引入都是用@Autowired
    @Autowired
    private FactoryDao factoryDao;


    @Override
    public void save(Factory factory) {
        factoryDao.insertSelective(factory);
    }

    @Override
    public void update(Factory factory) {
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    @Override
    public void delete(String id) {
        factoryDao.deleteByPrimaryKey(id);
    }

    @Override
    public Factory findById(String id) {
        return factoryDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Factory> findAll(FactoryExample example) {
        return factoryDao.selectByExample(example);
    }

    @Override
    public PageInfo findByPage(int pageNum, int pageSize, FactoryExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<Factory> factories = factoryDao.selectByExample(example);
        return new PageInfo(factories, 10);
    }
}

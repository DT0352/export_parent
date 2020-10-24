package com.itheima.service.company.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.company.CompanyDao;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    @Override
    public void save(Company company) {
        companyDao.save(company);
    }

    @Override
    public Company findById(String id) {
        return companyDao.findById(id);
    }

    @Override
    public void update(Company company) {
        companyDao.update(company);
    }

    @Override
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }

//    @Override
//    public PageInfo findByPage(Integer pageNum, Integer pageSize) {
//
//        //1. 查询total 总记录数
//        Long total = companyDao.findTotal();
//
//        //2. 查询list  数据集合
//        int startIndex = (pageNum - 1) * pageSize;
//        List<Company> list = companyDao.findList(startIndex, pageSize);
//
//
//        return new PageInfo();
//    }


    @Override
    public PageInfo findByPage(Integer pageNum, Integer pageSize) {
        //1. 设置pageNum和pageSize
        PageHelper.startPage(pageNum,pageSize);

        //2. 调用一个查询所有的方法
        List<Company> list = companyDao.findAll();

        //3. 直接返回PageInfo
        return new PageInfo(list,10);
    }
}

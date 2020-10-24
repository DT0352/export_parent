package com.itheima.service.company;


import com.github.pagehelper.PageInfo;
import com.itheima.domain.company.Company;

import java.util.List;

public interface CompanyService {

    List<Company> findAll();

    void save(Company company);

    Company findById(String id);

    void update(Company company);

    void deleteById(String id);

    PageInfo findByPage(Integer pageNum, Integer pageSize);

}

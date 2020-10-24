package com.itheima.dao.company;

import com.itheima.domain.company.Company;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CompanyDao {

    //查询列表
    List<Company> findAll();

    void save(Company company);

    Company findById(String id);

    void update(Company company);

    void deleteById(String id);

    Long findTotal();

    List<Company> findList(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
}

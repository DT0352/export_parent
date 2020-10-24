package com.itheima.dao.system;


import com.itheima.domain.system.Dept;

import java.util.List;

public interface DeptDao {
    //companyId 此条件的作用是做企业隔离
    List<Dept> findAll(String companyId);

    void save(Dept dept);

    Dept findById(String id);

    void update(Dept dept);

    void deleteById(String id);

    List<Dept> findChildrenDept(String id);
}

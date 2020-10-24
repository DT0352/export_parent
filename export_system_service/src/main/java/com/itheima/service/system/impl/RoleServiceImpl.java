package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.RoleDao;
import com.itheima.domain.system.Role;
import com.itheima.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public void save(Role role) {
        roleDao.save(role);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public void delete(String id) {
        roleDao.delete(id);
    }

    public PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> list = roleDao.findAll(companyId);
        return new PageInfo<Role>(list, 5);
    }

    @Override
    public List<String> findModuleIdsByRoleId(String roleId) {
        return roleDao.findModuleIdsByRoleId(roleId);
    }

    @Override
    public void changeModule(String roleId, String[] moduleIds) {
        //1. 根据roleId删除中间表中当前角色对应的权限信息
        roleDao.deleteRoleModuleByRoleId(roleId);

        //2. 重新向中间表中添加角色和权限的对应信息
        if (moduleIds != null && moduleIds.length > 0) {
            for (String moduleId : moduleIds) {
                roleDao.saveRoleModule(roleId, moduleId);
            }
        }
    }
}
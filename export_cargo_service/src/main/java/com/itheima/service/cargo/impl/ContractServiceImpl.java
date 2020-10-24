package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ContractProductDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractService;
import com.itheima.vo.ContractProductVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ContractProductDao contractProductDao;

    @Autowired
    private ExtCproductDao extCproductDao;


    @Override
    public Contract findById(String id) {
        return contractDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Contract contract) {
        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    //条件是合同id
    @Override
    public void delete(String id) {
        //=========================================查询===========================================//
        //1. 查询当前合同下的所有附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractIdEqualTo(id);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        //2. 查询当前合同下的所有货物
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdEqualTo(id);
        List<ContractProduct> contractProductList = contractProductDao.selectByExample(contractProductExample);

        //========================================附件删除========================================//
        for (ExtCproduct extCproduct : extCproductList) {
            extCproductDao.deleteByPrimaryKey(extCproduct.getId());
        }

        //========================================货物删除========================================//
        for (ContractProduct contractProduct : contractProductList) {
            contractProductDao.deleteByPrimaryKey(contractProduct.getId());
        }

        //========================================合同删除========================================//
        contractDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<Contract> findAll(ContractExample example) {
        return contractDao.selectByExample(example);
    }

    @Override
    public PageInfo findByPage(int pageNum, int pageSize, ContractExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<Contract> list = contractDao.selectByExample(example);
        return new PageInfo(list, 10);
    }

    @Override
    public List<ContractProductVo> findContractProductVo(String inputDate, String companyId) {
        return contractDao.findContractProductVo(inputDate, companyId);
    }
}

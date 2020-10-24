package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ContractProductDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContractProductServiceImpl implements ContractProductService {

    @Autowired
    private ContractProductDao contractProductDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ExtCproductDao extCproductDao;

    @Override
    public void save(ContractProduct contractProduct) {
        //==========================================查询=======================================//
        //查询合同信息(条件是合同id)
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());

        //==========================================附件无影响=======================================//
        //==========================================货物新增=======================================//
        //1. 计算出小计金额  单价  *  数量
        Double amount = contractProduct.getPrice() * contractProduct.getCnumber();
        contractProduct.setAmount(amount);

        //2. 执行保存
        contractProductDao.insertSelective(contractProduct);

        //==========================================合同修改=======================================//
        //1. 合同中的货物种数+1
        contract.setProNum(contract.getProNum() + 1);

        //2. 合同中的总金额 + 货物小计金额
        contract.setTotalAmount(contract.getTotalAmount() + amount);

        //3. 合同修改
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void update(ContractProduct contractProduct) {
        //==========================================查询=======================================//
        //1. 查询当前货物对应的合同信息
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //2. 根据货物id查询原有货物信息
        ContractProduct contractProductOld = contractProductDao.selectByPrimaryKey(contractProduct.getId());

        //==========================================附件无影响=======================================//
        //==========================================货物修改=======================================//
        //1. 重新计算小计金额  单价 * 数量
        Double amount = contractProduct.getPrice() * contractProduct.getCnumber();
        contractProduct.setAmount(amount);

        //2. 执行修改
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        //==========================================合同修改=======================================//
        //1. 修改总金额  =  原总金额  - 货物原有小计 + 现有小计
        contract.setTotalAmount(contract.getTotalAmount() - contractProductOld.getAmount() + amount);

        //2. 执行修改
        contractDao.updateByPrimaryKeySelective(contract);
    }

    //条件是货物id
    @Override
    public void delete(String id) {
        //==========================================查询=======================================//
        //1. 获取当前货物下所有的附件信息(条件是货物id)
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(id);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        //2. 根据id查询货物的信息
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);

        //3. 根据货物种的合同id查询合同信息
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());


        //==========================================附件删除=======================================//
        //遍历删除
        for (ExtCproduct extCproduct : extCproductList) {
            extCproductDao.deleteByPrimaryKey(extCproduct.getId());
        }

        //==========================================货物删除=======================================//
        //主键删除
        contractProductDao.deleteByPrimaryKey(id);


        //==========================================合同修改=======================================//
        //1. 调整合同中的货物种数 - 1
        contract.setProNum(contract.getProNum() - 1);

        //2. 调整合同中的附件种数 -extCproductList.size()
        contract.setExtNum(contract.getExtNum() - extCproductList.size());

        //3 .调整合同总金额 - (货物金额 + 所有附件金额)
        double amount = 0d;

        amount += contractProduct.getAmount();//货物金额

        for (ExtCproduct extCproduct : extCproductList) {
            amount += extCproduct.getAmount();
        }
        contract.setTotalAmount(contract.getTotalAmount() - amount);

        //4 执行修改
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ContractProduct> findAll(ContractProductExample example) {
        return contractProductDao.selectByExample(example);
    }

    @Override
    public PageInfo findByPage(int pageNum, int pageSize, ContractProductExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<ContractProduct> list = contractProductDao.selectByExample(example);
        return new PageInfo(list, 10);
    }

    @Override
    public void patchSave(List<ContractProduct> list) {
        for (ContractProduct contractProduct : list) {
            this.save(contractProduct);
        }
    }
}

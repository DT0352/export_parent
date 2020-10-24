package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.*;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ExportService;
import com.itheima.vo.ExportProductResult;
import com.itheima.vo.ExportProductVo;
import com.itheima.vo.ExportResult;
import com.itheima.vo.ExportVo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao; // 报运dao

    @Autowired
    private ExportProductDao exportProductDao;//报运单商品dao

    @Autowired
    private ExtEproductDao extEproductDao;  //报运单附件Dao

    @Autowired
    private ContractDao contractDao; // 合同dao

    @Autowired
    private ContractProductDao contractProductDao; //合同货物dao

    @Autowired
    private ExtCproductDao extCproductDao;  //合同附件Dao

    //保存
    public void save(Export export) {

        //=========================查询===================================//
        List<String> contractIdList = Arrays.asList(export.getContractIds().split(","));

        //=========================查询合同信息,封装报运单信息===================================//
        //1. 查询当前报运单下的合同
        ContractExample contractExample = new ContractExample();
        contractExample.createCriteria().andIdIn(contractIdList);
        List<Contract> contractList = contractDao.selectByExample(contractExample);

        //2 拼接信息
        StringBuilder sb = new StringBuilder();
        Integer proNum = 0;
        Integer extNum = 0;
        for (Contract contract : contractList) {
            sb.append(contract.getContractNo()).append(" ");
            proNum += contract.getProNum();
            extNum += contract.getExtNum();
        }
        export.setCustomerContract(sb.toString());
        export.setProNum(proNum);
        export.setExtNum(extNum);

        //3. 执行报运单保存
        exportDao.insertSelective(export);


        //=========================查询合同货物信息,封装报运单货物信息===================================//
        //1. 先查询当前报运单下的合同中的货物
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdIn(contractIdList);
        List<ContractProduct> contractProductList = contractProductDao.selectByExample(contractProductExample);

        //2. 数据封装
        for (ContractProduct contractProduct : contractProductList) {
            //复制对象的属性
            //注意:1.属性的复制一定在自己赋值之前  2使用springframework的BeanUtils
            ExportProduct exportProduct = new ExportProduct();
            BeanUtils.copyProperties(contractProduct, exportProduct);

            //设置id
            exportProduct.setId(UUID.randomUUID().toString());
            //设置exportId
            exportProduct.setExportId(export.getId());

            //3. 数据保存
            exportProductDao.insertSelective(exportProduct);
        }


        //=========================查询合同附件信息,封装报运单附件信息===================================//
        //1. 先查询当前报运单下的合同中的附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractIdIn(contractIdList);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        //2. 数据封装
        for (ExtCproduct extCproduct : extCproductList) {
            //复制对象数据
            ExtEproduct extEproduct = new ExtEproduct();
            BeanUtils.copyProperties(extCproduct, extEproduct);
            //设置id
            extEproduct.setId(UUID.randomUUID().toString());
            //设置exportid
            extEproduct.setExportId(export.getId());

            //3. 数据保存
            extEproductDao.insertSelective(extEproduct);
        }

    }

    //更新
    public void update(Export export) {
        //1. 修改报运单信息
        exportDao.updateByPrimaryKeySelective(export);

        for (ExportProduct exportProduct : export.getExportProducts()) {
            //2. 修改报运单下的货物信息
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
    }

    //删除
    public void delete(String id) {

    }

    //根据id查询
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    //分页
    public PageInfo findByPage(int pageNum, int pageSize, ExportExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<Export> list = exportDao.selectByExample(example);
        return new PageInfo(list);
    }

    @Override
    public void exportE(String id) {
        //1. 查询报运单信息,封装成ExportVo
        Export export = exportDao.selectByPrimaryKey(id);
        ExportVo exportVo = new ExportVo();
        BeanUtils.copyProperties(export, exportVo);
        exportVo.setExportId(id);
        exportVo.setExportDate(new Date());


        //2. 查询报运单下的货物信息, 封装成ExportProductVo
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> exportProductList = exportProductDao.selectByExample(exportProductExample);
        for (ExportProduct exportProduct : exportProductList) {
            ExportProductVo exportProductVo = new ExportProductVo();
            BeanUtils.copyProperties(exportProduct, exportProductVo);
            exportProductVo.setExportProductId(exportProduct.getId());

            exportVo.getProducts().add(exportProductVo);
        }

        //3. 调用海关平台,然后将ExportVo发送出去
        WebClient.create("http://localhost:5003/ws/export/user").post(exportVo);

        //4. 修改当前报运单的状态(0-草稿  1-已上报  2-已报运)
        export.setState(1);
        exportDao.updateByPrimaryKeySelective(export);
    }

    @Override
    public void findExportResult(String id) {
        try {
            //1.调用接口查询报运单审核状态
            ExportResult exportResult = WebClient.create("http://localhost:5003/ws/export/user/" + id).get(ExportResult.class);

            //2. 根据exportResult对象更新报运单中的信息
            Export export = new Export();
            export.setId(id);
            export.setState(exportResult.getState());
            export.setRemark(exportResult.getRemark());
            exportDao.updateByPrimaryKeySelective(export);

            //3. 根据exportResult对象中的products更新报运单中的信息
            for (ExportProductResult exportProductResult : exportResult.getProducts()) {
                ExportProduct exportProduct = new ExportProduct();
                exportProduct.setId(exportProductResult.getExportProductId());
                exportProduct.setTax(exportProductResult.getTax());
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询海关数据失败,请检查");
        }
    }

}

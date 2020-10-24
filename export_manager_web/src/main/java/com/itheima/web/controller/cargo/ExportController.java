package com.itheima.web.controller.cargo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractService;
import com.itheima.service.cargo.ExportProductService;
import com.itheima.service.cargo.ExportService;
import com.itheima.utils.DownloadUtil;
import com.itheima.web.controller.BaseController;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {

    @Reference
    private ContractService contractService;

    @Reference
    private ExportService exportService;

    @Reference
    private ExportProductService exportProductService;

    @RequestMapping(value = "/contractList", name = "已提交状态的合同列表查询")
    public String contractList(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        ContractExample contractExample = new ContractExample();
        ContractExample.Criteria criteria = contractExample.createCriteria();
        criteria.andCompanyIdEqualTo(getCompanyId());
        criteria.andStateEqualTo(1);

        PageInfo pageInfo = contractService.findByPage(pageNum, pageSize, contractExample);
        request.setAttribute("page", pageInfo);

        return "/cargo/export/export-contractList";
    }


    @RequestMapping(value = "/list", name = "报运单列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {


        ExportExample exportExample = new ExportExample();
        ExportExample.Criteria criteria = exportExample.createCriteria();
        criteria.andCompanyIdEqualTo(getCompanyId());//企业id


        PageInfo pageInfo = exportService.findByPage(pageNum, pageSize, exportExample);
        request.setAttribute("page", pageInfo);

        return "/cargo/export/export-list";
    }


    @RequestMapping(value = "/toExport", name = "跳转报运单新增页面")
    public String toAdd(String id) {
        //回显合同id集合
        request.setAttribute("id", id);
        return "/cargo/export/export-toExport";
    }


    @RequestMapping(value = "/toUpdate", name = "跳转报运单编辑页面")
    public String toUpdate(String id) {
        //1. 根据id查询当前报运单信息
        Export export = exportService.findById(id);
        request.setAttribute("export", export);

        //2. 根据报运单id查询当前报运单下的所有货物
        List<ExportProduct> eps = exportProductService.findByExportId(id);
        request.setAttribute("eps", eps);

        //3. 转发到修改页面
        return "/cargo/export/export-update";
    }

    @RequestMapping(value = "/edit", name = "报运单新增")
    public String edit(Export export) {
        if (StringUtils.isEmpty(export.getId())) {
            //1. 设置主键
            export.setId(UUID.randomUUID().toString());

            //2. 设置企业信息
            export.setCompanyId(getCompanyId());
            export.setCompanyName(getCompanyName());

            //3.制单时间
            export.setInputDate(new Date());

            //4. 设置当前合同的状态
            export.setState(0);//草稿

            exportService.save(export);
        } else {
            exportService.update(export);
        }

        //重定向到list方法
        return "redirect:/cargo/export/list.do";
    }
//
//
//    @RequestMapping(value = "/delete", name = "报运单删除")
//    public String delete(String id) {
//        //调用service删除
//        contractService.delete(id);
//
//        //重定向到list方法
//        return "redirect:/cargo/export/list.do";
//    }
//


    @RequestMapping(value = "/exportE", name = "海关电子报运")
    public String exportE(String id) {
        exportService.exportE(id);

        //重定向到list方法
        return "redirect:/cargo/export/list.do";
    }


    @RequestMapping(value = "/findExportResult", name = "查询海关电子报运结果")
    public String findExportResult(String id) {
        exportService.findExportResult(id);

        //重定向到list方法
        return "redirect:/cargo/export/list.do";
    }


    @RequestMapping(value = "/exportPdf", name = "报运单下载")
    public void exportPdf(String id) throws JRException, IOException {
        //0. 获取模板
        String realPath = session.getServletContext().getRealPath("/jasper/demo.jasper");

        //1. 获取数据 条件 报运单id    结果 报运单信息和报运单下货物信息
        //1-1) 报运单信息
        Export export = exportService.findById(id);
        Map<String, Object> map = BeanUtil.beanToMap(export);

        //1-2) 货物信息
        List<ExportProduct> list = exportProductService.findByExportId(id);
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2  向模板填充数据
        JasperPrint jasperPrint = JasperFillManager.fillReport(realPath, map, dataSource);
        //3.输出到浏览器
        //JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        //3. 文件下载
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        DownloadUtil.download(byteArrayOutputStream,response,"报运单.pdf");
    }
}

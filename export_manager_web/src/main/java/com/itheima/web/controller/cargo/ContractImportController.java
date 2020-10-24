package com.itheima.web.controller.cargo;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.service.cargo.ContractService;
import com.itheima.utils.DownloadUtil;
import com.itheima.vo.ContractProductVo;
import com.itheima.web.controller.BaseController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author ZhiDong
 * <p>
 * 2020/10/15
 */
@Controller
@RequestMapping("/cargo/contract")
public class ContractImportController extends BaseController {
    @Reference
    private ContractService contractService;

//    @RequestMapping(value = "/print", name = "出货表的导出")
//    public String contractPrint() {
//        return "/cargo/print/contract-print";
//    }

    @RequestMapping(value = "/printExcel", name = "出货表的导出")
    public void printExcel(String inputDate) throws IOException {

//       获取查询结果
        try {
            Date parse = new SimpleDateFormat("yyyy-MM").parse(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException("日期输入错误");
        }
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());
//        创建表格
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
//        合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
        for (int i = 1; i < 9; i++) {
            sheet.setColumnWidth(i, 25 * 256);
        }
        Row row0 = sheet.createRow(0);
        for (int i = 1; i < 9; i++) {
            row0.createCell(i);
        }
        String tittle = inputDate.replace("-0", "年").replace("-", "年") + "月份出货表";
//        设置标题
        row0.getCell(1).setCellValue(tittle);
        String[] header = {"客户", "合同号", "货号", "数量", "工厂", "工厂交期", "船期", "贸易条款"};
        Row row1 = sheet.createRow(1);
        for (int i = 1; i < 9; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellValue(header[i - 1]);
        }
//        创建n行数据
        int n = 2;
        for (ContractProductVo contractProductVo : list) {
            Row rowN = sheet.createRow(n++);
            Object[] productVo = contractProductVo.getContractProductVo();
            for (int i = 1; i < 9; i++) {
                Cell cell = rowN.createCell(i);
                cell.setCellValue(String.valueOf(productVo[i - 1]));
            }
        }
        //3. 文件下载
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        DownloadUtil.download(outputStream, response, "出货表.xlsx");

    }

    @RequestMapping(value = "/printExcelWithTemplate", name = "出货表的模板导出")
    public void printExcelWithTemplate(String inputDate) throws IOException {
//        获取参数
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());
        String realPath = session.getServletContext().getRealPath("make/xlsprint/tOUTPRODUCT.xlsx");
        Workbook workbook = new XSSFWorkbook(new FileInputStream(realPath));
        Sheet sheet = workbook.getSheetAt(0);
        String tittle = inputDate.replace("-0", "年").replace("-", "年") + "月份出货表";
//        设置标题
        sheet.getRow(0).getCell(1).setCellValue(tittle);
        Row row2 = sheet.getRow(2);
        CellStyle[] cellStyles = new CellStyle[8];
        for (int i = 1; i < 9; i++) {
            cellStyles[i - 1] = row2.getCell(i).getCellStyle();
        }
//        填充数据
        int n = 2;
        for (ContractProductVo contractProductVo : list) {
            Row rowN = sheet.createRow(n++);
            Object[] productVo = contractProductVo.getContractProductVo();
            for (int i = 1; i < 9; i++) {
                Cell cell = rowN.createCell(i);
                cell.setCellValue(String.valueOf(productVo[i - 1]));
                cell.setCellStyle(cellStyles[i - 1]);
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        DownloadUtil.download(byteArrayOutputStream, response, "模板出货表.xlsx");
    }

    @RequestMapping(value = "/printExcelMillion", name = "百万出货表的模板导出")
    public void printExcelMillion(String inputDate) throws IOException {
//       获取查询结果
        try {
            Date parse = new SimpleDateFormat("yyyy-MM").parse(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException("日期输入错误");
        }
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());
//        创建表格
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
//        合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
        for (int i = 1; i < 9; i++) {
            sheet.setColumnWidth(i, 25 * 256);
        }
        Row row0 = sheet.createRow(0);
        for (int i = 1; i < 9; i++) {
            row0.createCell(i);
        }
        String tittle = inputDate.replace("-0", "年").replace("-", "年") + "月份出货表";
//        设置标题
        row0.getCell(1).setCellValue(tittle);
        String[] header = {"客户", "合同号", "货号", "数量", "工厂", "工厂交期", "船期", "贸易条款"};
        Row row1 = sheet.createRow(1);
        for (int i = 1; i < 9; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellValue(header[i - 1]);
        }
//        创建n行数据
        int n = 2;
        for (int j = 0; j < 800; j++) {
            for (ContractProductVo contractProductVo : list) {
                Row rowN = sheet.createRow(n++);
                Object[] productVo = contractProductVo.getContractProductVo();
                for (int i = 1; i < 9; i++) {
                    Cell cell = rowN.createCell(i);
                    cell.setCellValue(String.valueOf(productVo[i - 1]));
                }
            }
        }
        //3. 文件下载
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        DownloadUtil.download(outputStream, response, "出货表.xlsx");
    }
}

package com.itheima.service.cargo.job;

import com.itheima.dao.cargo.ExportDao;
import com.itheima.dao.cargo.ExportProductDao;
import com.itheima.domain.cargo.Export;
import com.itheima.domain.cargo.ExportExample;
import com.itheima.domain.cargo.ExportProduct;
import com.itheima.vo.ExportProductResult;
import com.itheima.vo.ExportResult;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author ZhiDong
 * <p>
 * 2020/10/21
 */
@Component
public class ExportJob {
    @Autowired
    private ExportDao exportDao;
    @Autowired
    private ExportProductDao exportProductDao;

    @Scheduled(cron = "0/5 * * * * ?")
    public void findExportResult() {
        try {
            //1.调用接口查询报运单审核状态

            ExportExample exportExample = new ExportExample();
            exportExample.createCriteria().andStateEqualTo(1L);
            List<Export> exports = exportDao.selectByExample(exportExample);

            for (Export export : exports) {
                ExportResult exportResult = WebClient.create("http://192.168.12.139:5003/ws/export/user/" + export.getId()).get(ExportResult.class);
                export.setState(exportResult.getState());
                export.setRemark(exportResult.getRemark());
                exportDao.updateByPrimaryKeySelective(export);
                Set<ExportProductResult> exportProducts = exportResult.getProducts();
                for (ExportProductResult exportProductResult : exportProducts) {
                    ExportProduct exportProduct = new ExportProduct();
                    exportProduct.setId(exportProductResult.getExportProductId());
                    exportProduct.setTax(exportProductResult.getTax());
                    exportProductDao.updateByPrimaryKeySelective(exportProduct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询海关数据失败,请检查");
        }
    }

}

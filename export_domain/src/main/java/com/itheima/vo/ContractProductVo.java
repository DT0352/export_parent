package com.itheima.vo;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ContractProductVo implements Serializable {

    private String customName;		//客户名称
    private String contractNo;		//合同号，订单号
    private String productNo;		//货号
    private Integer cnumber;		//数量
    private String factoryName;		//厂家名称
    private Date deliveryPeriod;	//交货期限
    private Date shipTime;			//船期
    private String tradeTerms;		//贸易条款

    public Object[] getContractProductVo() {
        Object[] objects = new Object[8];
        objects[0] = this.getCustomName();
        objects[1] = this.getContractNo();
        objects[2] = this.getProductNo();
        objects[3] = this.getCnumber();
        objects[4] = this.getFactoryName();
        objects[5] = this.getDeliveryPeriod();
        objects[6] = this.getShipTime();
        objects[7] = this.getTradeTerms();
        return objects;

    }
}
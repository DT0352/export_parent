package com.itheima.domain.company;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class Company implements Serializable {

    //1. 属性名必须是驼峰
    //2. 注意属性数据类型
    private String id;
    private String name;
    //类型转换器:
    //1.编码形式: 实现麻烦, 只需要实现一次
    //2.注解形式: 实现简单, 每个实体类中,只要有类型需要转换都要加这个注解
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirationDate;
    private String address;
    private String licenseId;
    private String representative;
    private String phone;
    private String companySize;
    private String industry;
    private String remarks;
    private Integer state;
    private Double balance;
    private String city;
}

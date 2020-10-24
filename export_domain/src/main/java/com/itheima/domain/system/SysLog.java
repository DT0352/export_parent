package com.itheima.domain.system;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class SysLog implements Serializable {
    private String id;//主键
    private String userName;//session
    private String ip;//request
    private Date time;//当前时间
    private String method;//方法名 通过环绕通知的切点就可以获取到
    private String action;//操作   通过环绕通知的切点就可以获取到
    private String companyId;//session
    private String companyName;//session
}
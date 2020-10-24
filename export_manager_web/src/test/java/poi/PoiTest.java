package poi;

import cn.hutool.extra.mail.MailUtil;

import java.io.File;

/**
 * @author ZhiDong
 * <p>
 * 2020/10/17
 */
public class PoiTest {
    public static void main(String[] args) {
            MailUtil.send("1933759139@qq.com","这是一个测试邮件" ,"你猜啊",false,new File("D:\\dev\\export_parent\\export_manager_web\\src\\main\\java\\com\\itheima\\web\\realm\\SaasRealm.java"));
    }

}

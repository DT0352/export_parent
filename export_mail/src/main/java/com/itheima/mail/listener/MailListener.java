package com.itheima.mail.listener;

import com.alibaba.fastjson.JSONObject;
import com.itheima.utils.MailUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.Map;

public class MailListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        //接收消息,发送邮件
        Map map = JSONObject.parseObject(message.getBody(), Map.class);

        String to = (String) map.get("to");
        String title = (String) map.get("title");
        String content = (String) map.get("content");

        System.out.println(title);

        //调用工具类发送邮件
        MailUtil.sendMail(to, title, content);

    }
}

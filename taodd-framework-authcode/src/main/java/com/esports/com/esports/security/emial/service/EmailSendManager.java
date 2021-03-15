package com.esports.com.esports.security.emial.service;

import com.esports.com.esports.security.client.EmailClient;
import com.esports.com.esports.security.client.SmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailSendManager {

    @Autowired
    private EmailClient emailClient;

    public void sendemail(String email, String code){
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(code)){
            return;
        }
        emailClient.sendEmail(email, code);
    }

    public String getEmailRedisPrefix(){
        String prefix = emailClient.getPrefix();
        return prefix;
    }

    public Long getEmailExpireTime(){
        Long expireTime = emailClient.getExpire();
        return expireTime;
    }
}

package com.esports.com.esports.security.sms.service;

import com.esports.com.esports.security.client.SmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SmsSendManager {

    @Autowired
    private SmsClient smsClient;

    public void sendsms(String mobile, String code){
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)){
            return;
        }
        smsClient.sendSms(mobile, code);
    }

    public String getSmsRedisPrefix(){
        String prefix = smsClient.getPrefix();
        return prefix;
    }

    public Long getSmsExpireTime(){
        Long expireTime = smsClient.getExpire();
        return expireTime;
    }

    public Integer getSmsSendTimeInterval(){
        Integer timeInterval = smsClient.getSendSeconds();
        return timeInterval;
    }

    public Integer getSmsSendTotal(){
        Integer smsTotal = smsClient.getDailyCount();
        return smsTotal;
    }
}

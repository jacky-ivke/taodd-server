package com.esports.com.esports.security.code.service;

import com.esports.cache.RedisUtil;
import com.esports.com.esports.security.client.ValidateCodeImageClient;
import com.esports.com.esports.security.emial.service.EmailSendManager;
import com.esports.com.esports.security.sms.service.SmsSendManager;
import com.esports.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SecurityCodeManager {

    private static final long EXPIRE_TIME = 60;


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SmsSendManager smsEmailService;

    @Autowired
    private EmailSendManager emailSendManager;

    public JSONObject getImgBase64(String key) {
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("base64", "");
        if (StringUtils.isEmpty(key)) {
            return json;
        }
        ValidateCodeImageClient vCode = new ValidateCodeImageClient(120, 40, 4, 10);
        try {
            BufferedImage image = vCode.getBuffImg();
            String code = vCode.getCode();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            byte[] bytes = stream.toByteArray();// 转换成字节
            String base64 = DatatypeConverter.printBase64Binary(bytes);
            String redisKey = key;
            redisUtil.set(redisKey, code, EXPIRE_TIME);
            json.put("base64", base64);
        } catch (Exception e) {
        }
        return json;
    }

    public JSONObject getCode(String key) {
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("code", "");
        if (StringUtils.isEmpty(key)) {
            return json;
        }
        String code = NumberUtils.generateVerifyCode();
        String redisKey = key;
        redisUtil.set(redisKey, code, EXPIRE_TIME);
        json.put("code", code);
        return json;
    }

    public boolean checkCode(String key, String code) {
        boolean flag = false;
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(code)) {
            return flag;
        }
        String redisKey = key;
        if (redisUtil.hasKey(redisKey)) {
            String redisCode = redisUtil.get(redisKey).toString();
            if (redisCode.equalsIgnoreCase(code)) {
                flag = true;
                redisUtil.del(redisKey);
            }
        }
        return flag;
    }

    public String sendSmsCode(String mobile) {
        log.info("【开始发送短信验证码，发送手机：{}】", mobile);
        String code = NumberUtils.generateVerifyCode();
        smsEmailService.sendsms(mobile, code);
        String redisKey = smsEmailService.getSmsRedisPrefix() + mobile;
        redisUtil.set(redisKey, code, smsEmailService.getSmsExpireTime());
        log.info("【短信验证码发送成功，发送手机：{}， 验证码：{}， rediskey:{}】", mobile, code, redisKey);
        return code;
    }

    public String sendEmailCode(String email) {
        log.info("【开始发送邮箱证码，发送邮箱：{}】", email);
        String code = NumberUtils.generateVerifyCode();
        emailSendManager.sendemail(email, code);
        String redisKey = emailSendManager.getEmailRedisPrefix() + email;
        redisUtil.set(redisKey, code, emailSendManager.getEmailExpireTime());
        log.info("【邮箱验证码发送成功，发送邮箱：{}， 验证码：{}， rediskey:{}】", email, code, redisKey);
        return code;
    }

    public boolean checkEmailCode(String email, String code) {
        boolean success = false;
        String redisKey = emailSendManager.getEmailRedisPrefix() + email;
        if (!redisUtil.hasKey(redisKey)) {
            return success;
        }
        String redisCode = redisUtil.get(redisKey).toString();
        if (redisCode.equalsIgnoreCase(code)) {
            success = true;
            redisUtil.del(redisKey);
        }
        return success;
    }

    public boolean checkSmsCode(String mobile, String code) {
        boolean success = false;
        String redisKey = smsEmailService.getSmsRedisPrefix() + mobile;
        if (!redisUtil.hasKey(redisKey)) {
            return success;
        }
        String redisCode = redisUtil.get(redisKey).toString();
        if (redisCode.equalsIgnoreCase(code)) {
            success = true;
            redisUtil.del(redisKey);
        }
        return success;
    }

    public Integer isAuthCodeSend(String mobile) {
        List<String> keyList = new ArrayList<String>();
        Integer result = null;
        try {
            keyList.add(mobile);
            keyList.add(String.valueOf(smsEmailService.getSmsSendTimeInterval()));
            keyList.add(String.valueOf(smsEmailService.getSmsSendTotal()));
            result = redisUtil.sendMsg(mobile, keyList);
        } catch (Exception e) {
            log.error("【短信验证码安全限制出错】异常信息:{}", e.getMessage());
        }
        return result;
    }
}

 package com.esports.com.esports.security.client;


 import com.esports.network.HttpClientUtils;
 import lombok.Data;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.codec.binary.Base64;
 import org.springframework.boot.context.properties.ConfigurationProperties;
 import org.springframework.context.annotation.PropertySource;
 import org.springframework.stereotype.Component;
 import org.springframework.util.StringUtils;

 import java.io.UnsupportedEncodingException;
 import java.net.URLEncoder;

 @Component
 @PropertySource("classpath:smsemail.properties")
 @ConfigurationProperties(prefix = "sms")
 @Data
 @Slf4j
 public class SmsClient {

     private long expire;

     private int dailyCount;

     private int sendSeconds;

     private String prefix;

     private String message;

     private String url;

     private String username;

     private String password;

     private static String CHARSET = "GBK";

     protected String setEncoding(String message, String charset){
         String encode = "";
         if (!StringUtils.isEmpty(message)) {
             try {
                 encode = URLEncoder.encode(message, charset);
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }
         }
         return encode;
     }

     public void sendSms(String mobile, String code) {
         if(StringUtils.isEmpty(mobile)) {
             return;
         }
         StringBuilder buffer = new StringBuilder();
         String content = String.format(message, code);
         buffer.append(this.url)
                 .append("username=").append(this.username)
                 .append("&")
                 .append("password=").append(Base64.encodeBase64String(this.password.getBytes()))
                 .append("&")
                 .append("mobile=").append(mobile)
                 .append("&")
                 .append("content=").append(this.setEncoding(content, CHARSET));
         try {
             String result = HttpClientUtils.asyncGet(buffer.toString(), CHARSET);
             log.info("【短信发送成功，返回结果：{}】", result);
         } catch (Exception e) {
             log.error("【短信发送失败，异常信息:{}】", e.getMessage());
         }
     }

 }

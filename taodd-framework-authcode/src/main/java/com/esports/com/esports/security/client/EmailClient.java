package com.esports.com.esports.security.client;

import com.esports.processor.ApiGateway;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Component
@PropertySource("classpath:smsemail.properties")
@ConfigurationProperties(prefix = "email")
@Data
@Slf4j
public class EmailClient {

    private String prefix;

    private String host;

    private String username;

    private String password;

    private String subject;

    private String messageText;

    private long expire;

    public void sendEmail(String email, String code) {
        String host = this.getHost();
        String username = this.getUsername();
        String password = this.getPassword();
        String messageText = String.format(this.getMessageText(), code);
        String subject = this.getSubject();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(
            new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    log.info("========================【邮件处理】 ======================");
                    try {
                        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                        mailSender.setHost(host);
                        mailSender.setUsername(username);
                        mailSender.setPassword(password);
                        mailSender.setDefaultEncoding("UTF-8");
                        //加认证机制
                        Properties javaMailProperties = new Properties();
                        javaMailProperties.put("mail.smtp.auth", true);
                        javaMailProperties.put("mail.smtp.starttls.enable", true);
                        javaMailProperties.put("mail.smtp.timeout", 60 * 1000);
                        javaMailProperties.put("mail.smtp.ssl.enable", "true");
                        mailSender.setJavaMailProperties(javaMailProperties);
                        //创建邮件内容
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setFrom(username);
                        message.setTo(email);
                        message.setSubject(subject);
                        message.setText(messageText);
                        //发送邮件
                        mailSender.send(message);
                        log.info("========================【邮件发送成功】 ======================");
                    }catch (Exception e){
                        log.info("========================【发送失败：{}】 ======================", e);
                    }

                    return 1;
                }
            });
        ApiGateway.executorService.submit(futureTask);
    }
}

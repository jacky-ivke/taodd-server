package com.esports.utils;

import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Random;

public class NumberUtils {
	
    private static final String SYMBOLS = "0123456789";
    
    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成验证码
     *
     * @param
     * @return
     */
    public synchronized static String generateVerifyCode() {
        char[] nonceChars = new char[6];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    public static String generateOrderId() {
        return "";
    }

    /**
     * @Ttile:
     * @Desciption: 打*号,字符串从第一几位开始,最后保留几位,中间全是**
     * @Author: Administrator
     * @Param:
     * @Date: 2018/11/13 0013   16:14
     * @Version:1.0
     * @return:
     **/
    public static String disableNumber(String value, int startNum, int lastNum) {
        if(StringUtils.isEmpty(value)) {
            return "";
        }
        char[] chars = new char[value.length()];
        value.getChars(0, value.length(), chars, 0);
        for (int i = startNum - 1; i < chars.length - lastNum; i++) {
            chars[i] = '*';
        }
        return String.valueOf(chars);
    }

    public static String strDisable(String str) {
        int i= 4;
        if (StringUtils.isEmpty(str) || str.length()<=i){
            return str;
        }
        int length = str.length();
        //算出中间四位的下标
        int m = length/2;
        int ys = length%2;
        if (ys==0){
            str = NumberUtils.disableNumber(str, m-1 , m-2  );
        }else {
            str = NumberUtils.disableNumber(str, m , m-2  );
        }

        return str;
    }
    
    public static String hideName(String realName) {
        if (StringUtils.isEmpty(realName)){
            return "未认证";
        }
        if(realName.length()<=2) {
        	realName = realName.replaceAll("(.{1})(.*)(.{0})", "$1*$3");  
        }else {
        	realName = realName.replaceAll("(.{1})(.*)(.{1})", "$1*$3");  
        }
        return realName;
    }


    public static String hideMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)){
            return mobile;
        }
        mobile = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return mobile;
    }
    
    public static String hideEmai(String email) {
        if (StringUtils.isEmpty(email)){
            return email;
        }
        email= email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)","$1****$3$4");
        return email;
    }
    
    public static String hideBankAccount(String bankAccount) {
    	  if (StringUtils.isEmpty(bankAccount)){
              return bankAccount;
          }
    	  bankAccount = bankAccount.replaceAll("^(\\d{4})\\d+(\\d{4})$", "$1****$2");
          return bankAccount;
    }

    public static void main(String[] args) {
    }

}

package com.esports.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * AES加解密算法
 */
public final class AESUtil {
    //默认密钥 
    private static final String KEY = "febcbaae13751f7f";
    //默认的加密算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    /**
     * aes解密
     *
     * @param encrypt 内容
     */
    public static String decrypt(String encrypt) {
        return decrypt(encrypt, KEY);
    }

    /**
     * aes加密
     *
     * @param content
     */
    public static String encrypt(String content) {
        return encrypt(content, KEY);
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtils.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code);
    }


    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] encryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    public static String encrypt(String content, String encryptKey) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        try {
            return base64Encode(encryptToBytes(content, encryptKey));
        } catch (Exception e) {
            logger.warn("AES加密失败", e);
            return "";
        }
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String decryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }


    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String decrypt(String encryptStr, String decryptKey) {
        if (StringUtils.isEmpty(encryptStr)) {
            return "";
        }
        try {
            return decryptByBytes(base64Decode(encryptStr), decryptKey);
        } catch (Exception e) {
            logger.warn("AES解密失败", e);
            return "";
        }
    }
    
    public static String encrypt(String data, String ascKey, String cipherAlgorithm, String offset) throws Exception {
    	if(StringUtils.isEmpty(data)) {
    		return "";
    	}
    	Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        SecretKeySpec skeySpec = new SecretKeySpec(ascKey.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64Encode(encrypted);//此处使用BASE64做转码。
    }

}

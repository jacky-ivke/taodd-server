package com.esports.utils;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
    private static final Logger logger = LoggerFactory.getLogger(Md5Utils.class);
    private static final char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String encrypt(String value) {
        return Hashing.sha256().hashBytes(value.getBytes()).toString();
    }

    public static String getMD5(String source) {
        byte[] temp = (source != null ? source : "").getBytes();
        return getMD5(temp);
    }

    public static String getMD5(String source, int count) {
        Preconditions.checkArgument(count > 0, "conut必须大于0");
        String str = source;
        for (int i = 0; i < count; i++) {
            str = getMD5(str);
        }
        return str;
    }

    public static String getMD5(byte[] source) {
        try {
            StringBuilder result = new StringBuilder();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(source);
            byte[] b = digest.digest();
            for (int i = 0; i < b.length; i++) {
                char[] ob = new char[2];
                ob[0] = digit[(b[i] >>> 4) & 0X0F];
                ob[1] = digit[b[i] & 0X0F];
                result.append(new String(ob));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5加密失败：" + source, e.getMessage());
            return "";
        }
    }
}

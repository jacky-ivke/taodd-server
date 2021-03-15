package com.esports.utils;

import org.springframework.util.StringUtils;

public class SignUtils {

	public static boolean checkTimestamp(Long time, Integer condition) {
		Long currentTimeMillis = System.currentTimeMillis();
		Long addTime = currentTimeMillis + condition;
		Long subTime = currentTimeMillis - condition;
        return addTime > time && time > subTime;
    }

	public static boolean verifySign(String params, String originalSign) {
		boolean success = false;
		if (StringUtils.isEmpty(params) || StringUtils.isEmpty(originalSign)) {
			return success;
		}
		String targetSign = createSign(params);
		if (!StringUtils.isEmpty(originalSign) && originalSign.equalsIgnoreCase(targetSign)) {
			success = true;
		}
		return success;
	}

	protected static String createSign(String params) {
		String sign = "";
		if (StringUtils.isEmpty(params)) {
			return sign;
		}
		sign = Md5Utils.getMD5(params);
		return sign;
	}
}

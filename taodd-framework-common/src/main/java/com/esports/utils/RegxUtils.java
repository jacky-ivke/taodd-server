package com.esports.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class RegxUtils {

	/**
	 * 正则匹配： 由字母和数字组成，但不能为纯数字，不能以test开头 验证用户账号,只能输入6-12位字母（区分大小写）、数字或下划线
	 * 
	 * @param account 用户账号，格式：ivke123
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkAccount(String account) {
//        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!\\d+$)[a-zA-Z0-9-_]{6,12}+$";
		String regex = "^\\b(?i)(?!test)(?![0-9]+$)(?!\\d+$)[a-zA-Z0-9-]{6,12}+$";
		return Pattern.matches(regex, account);
	}

	/**
	 * 验证登录密码,只能输入6-12位包含大小写字母、数字或字符
	 * 
	 * @param password 用户名，格式：ivke1234
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPassword(String password) {
		//String regex = "^[a-zA-Z0-9|_|*|@|%|#|^|.|,|!|$|&|^|-]{6,12}$";
		String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$";
		return Pattern.matches(regex, password);
	}

	/**
	 * 验证邀请码,只能输入数字或字母，最多可输入8位数
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkInvateCode(String code) {
		String regex = "^[a-zA-Z0-9]{1,20}$";
		return Pattern.matches(regex, code);
	}
	
	/**
	 * 验证金额
	 * @param amount
	 * @return
	 */
	public static boolean checkAmount(BigDecimal amount) {
		String regex = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
		return Pattern.matches(regex, String.valueOf(amount));
	}

	public static boolean checkMobile(String mobile) {
//		String regex = "^[1][3,4,5,7,8][0-9]{9}$";
//		return Pattern.matches(regex, String.valueOf(mobile));
		return true;
	}

	public static boolean checkLength(String str){
		String regex = "/^\\S{1,255}$/";
		return Pattern.matches(regex, str);
	}
}

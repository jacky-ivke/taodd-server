package com.esports.external.handler.xint.bbin.dto;

import com.esports.utils.Md5Utils;
import com.esports.utils.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {
	
	/**
	 * 网站名称
	 */
	private String website;
	
	/**
	 * 上层帐号
	 */
	private String uppername;

	/**
	 * 会员帐号
	 */
	private String username;
	
	/**
	 * 登录来源
	 */
	private Integer ingress=1;
	
	private String key;
	
	@JsonIgnore
	public String sign(String website, String username, String md5key) {
		final String utcTime = DateTime.now(DateTimeZone.forOffsetHours(-4)).toString("yyyyMMdd");
		final String keyA = RandomUtil.getRandomString(5);
		final String keyB = Md5Utils.getMD5(website+username+md5key+utcTime);
		final String keyC = RandomUtil.getRandomString(2);
		String sign = (keyA+keyB+keyC).toLowerCase();
		return sign;
	}
	
}

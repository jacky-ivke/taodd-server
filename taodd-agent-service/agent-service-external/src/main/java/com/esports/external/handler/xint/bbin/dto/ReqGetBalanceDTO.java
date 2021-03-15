package com.esports.external.handler.xint.bbin.dto;

import com.esports.utils.Md5Utils;
import com.esports.utils.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {
	
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
	 * 查询页数
	 */
	private Integer page=1;

	/**
	 * 每页数量
	 */
	private Integer pagelimit=20;
	
	private String key;
	
	@JsonIgnore
	public String sign(String website, String username, String md5key) {
		final String utcTime = DateTime.now(DateTimeZone.forOffsetHours(-4)).toString("yyyyMMdd");
		final String keyA = RandomUtil.getRandomString(9);
		final String keyB = Md5Utils.getMD5(website+username+md5key+utcTime);
		final String keyC = RandomUtil.getRandomString(6);
		String sign = (keyA+keyB+keyC+utcTime).toLowerCase();
		return sign;
	}
}

package com.esports.external.handler.xint.bbin.dto;

import com.esports.utils.Md5Utils;
import com.esports.utils.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 */
@Data
public class ReqTransferDTO{
	
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
	 * 转帐额度(正整数)
	 */
	@JsonProperty("remit")
	private Integer amount;
	
	@JsonProperty("remitno")
	private String orderNo;
	
	/**
	 * IN(转入额度) OUT(转出额度)
	 */
	private String action;
	
	private String key;
	
	@JsonIgnore
	public String sign(String website, String username, String orderNo, String md5key) {
		final String utcTime = DateTime.now(DateTimeZone.forOffsetHours(-4)).toString("yyyyMMdd");
		final String keyA = RandomUtil.getRandomString(2);
		final String keyB = Md5Utils.getMD5(website+username+orderNo+md5key+utcTime);
		final String keyC = RandomUtil.getRandomString(7);
		String sign = (keyA+keyB+keyC).toLowerCase();
		return sign;
	}
}

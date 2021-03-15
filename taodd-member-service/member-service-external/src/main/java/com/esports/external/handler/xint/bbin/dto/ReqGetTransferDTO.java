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
public class ReqGetTransferDTO{

	/**
	 * 网站名称
	 */
	private String website;
	
	private String uppername;
	
	private String username;
	
	@JsonProperty("date_start")
	private String startDate;
	
	@JsonProperty("date_end")
	private String endDate;
	
	
	/**
	 * 开始时间 ex：00:00:00
	 */
	@JsonProperty("start_hhmmss")
	private String startTime;
	
	/**
	 * 结束时间 ex：23:59:59
	 */
	@JsonProperty("end_hhmmss")
	private String endTime;
	
	@JsonProperty("transid")
	private String orderNo;
	
	private String key;
	
	@JsonIgnore
	public String sign(String website, String username, String md5key) {
		final String utcTime = DateTime.now(DateTimeZone.forOffsetHours(-4)).toString("yyyyMMdd");
		final String keyA = RandomUtil.getRandomString(5);
		final String keyB = Md5Utils.getMD5(website+username+md5key+utcTime);
		final String keyC = RandomUtil.getRandomString(4);
		String sign = (keyA+keyB+keyC).toLowerCase();
		return sign;
	}
}

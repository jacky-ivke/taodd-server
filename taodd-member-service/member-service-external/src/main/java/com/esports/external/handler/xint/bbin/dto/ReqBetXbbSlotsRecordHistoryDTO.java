package com.esports.external.handler.xint.bbin.dto;

import com.esports.utils.Md5Utils;
import com.esports.utils.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@Data
public class ReqBetXbbSlotsRecordHistoryDTO{
	
	/**
	 * 网站名称
	 */
	private String website;
	
	/**
	 * 上层帐号
	 */
	private String uppername;
	
	/**
	 * 使用下 注时间查询信息
	 */
	private String action = "BetTime";
	
	/**
	 * 日期 ex：2012/03/21、2012-03-21
	 */
	private String date;
	
	/**
	 * 开始时间 ex：00:00:00
	 */
	private String startTime;
	
	/**
	 * 结束时间 ex：23:59:59
	 */
	private String endTime;
	
	@JsonProperty("gametype")
	private String gameType="";
	
	private String subgamekind = "";
	
	@JsonProperty("page")
	private Long pageIndex;
	
	@JsonProperty("pagelimit")
	private Integer pageSize;
	
	private String key;
	
	@JsonIgnore
	public String sign(String website, String md5key) {
		final String utcTime = DateTime.now(DateTimeZone.forOffsetHours(-4)).toString("yyyyMMdd");
		final String keyA = RandomUtil.getRandomString(1);
		final String keyB = Md5Utils.getMD5(website+md5key+utcTime);
		final String keyC = RandomUtil.getRandomString(7);
		String sign = (keyA+keyB+keyC).toLowerCase();
		return sign;
	}
	
}

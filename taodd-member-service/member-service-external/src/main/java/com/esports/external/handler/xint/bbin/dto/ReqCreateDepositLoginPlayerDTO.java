package com.esports.external.handler.xint.bbin.dto;

import com.esports.constant.GameTypeCode;
import com.esports.constant.GlobalSourceCode;
import com.esports.utils.Md5Utils;
import com.esports.utils.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@Data
public class ReqCreateDepositLoginPlayerDTO {
	
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
	
	private String lang;
	
	/**
	 * BB 体育：ball、视讯：live、机率：game、彩票：Ltlottery、 New BB 体育：nball、BB 捕鱼达人、BB 捕鱼大师：fisharea， 若为空白则导入整合页
	 */
	@JsonProperty("page_site")
	private String pageSite;
	
	/**
	 * 视讯：live，导入视讯大厅页面，需同时带入 page_site
	 */
	@JsonProperty("page_present")
	private String pagePresent = "";
	
	/**
	 * 登入来源，请填入代码(1：网页版、2：手机网页版、4： App)，预设为 9：其他
	 */
	private Integer ingress;
	
	private String key;
	
	@JsonIgnore
	public String sign(String website, String username,String md5key) {
		final String utcTime = DateTime.now(DateTimeZone.forOffsetHours(-4)).toString("yyyyMMdd");
		final String keyA = RandomUtil.getRandomString(8);
		final String keyB = Md5Utils.getMD5(website+username+md5key+utcTime);
		final String keyC = RandomUtil.getRandomString(1);
		String sign = (keyA+keyB+keyC).toLowerCase();
		return sign;
	}
	
	@JsonIgnore
	public void setPageSite(String gameType) {
		if(GameTypeCode._FISH_ARCADE.getCode().equalsIgnoreCase(gameType)) {
			this.pageSite = "fisharea";
			return;
		}
		if(GameTypeCode._SPORTS.getCode().equalsIgnoreCase(gameType)) {
			this.pageSite = "ball";
			return;
		}
		if(GameTypeCode._LIVE.getCode().equalsIgnoreCase(gameType)) {
			this.pageSite = "live";
			this.pagePresent = "live";
			return;
		}
		if(GameTypeCode._SLOTS.getCode().equalsIgnoreCase(gameType)) {
			this.pageSite = "game";
			return;
		}
		if(GameTypeCode._LOTTO.getCode().equalsIgnoreCase(gameType)) {
			this.pageSite = "Ltlottery";
			return;
		}
	}
	
	@JsonIgnore
	public void setIngress(Integer deviceType) {
		if(GlobalSourceCode._H5.getCode().equals(deviceType.toString())) {
			this.ingress = 2;
			return;
		}else if(!GlobalSourceCode._PC.getCode().equals(deviceType.toString())) {
			this.ingress = 4;
			return;
		}
		this.ingress = 1;
	}
}

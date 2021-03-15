package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ReqSlotsCreateDepositLoginPlayerDTO {
   
	/**
	 * 随机字符串，建议使用UUID
	 */
	private String random;
	
	/**
	 * 签名摘要sign=md5(random+sn+secretKey)
	 */
	private String sign;
	
	/**
	 * 厅代码
	 */
	private String sn;
	
	/**
	 * 用户登录ID
	 */
	private String loginId="";
	
	/**
	 * 详见语言类别代码
	 */
	private String local;
	
	/**
	 * 是否返回手机端地址. 1:是; 0:否(默认)
	 */
	private Integer isMobileUrl=0;
	
	/**
	 * 是否返回https地址. 1,是(默认);0,否
	 */
	private Integer isHttpsUrl=0;
	
	/**
	 * 从游戏中退出时返回的地址，请使用UrlEncode进行编码示例: http%3a%2f%2fwww.bg567.com%2f
	 */
	private String returnUrl="";
	
	/**
	 * 终端类别
	 */
	private String orderFrom="";
	
	/**
	 * 游戏ID
	 */
	private String gameId="";
	
    @JsonIgnore
    public String getMethod() {
    	return "open.game.bg.egame.url";
    }
	
	
	
	
	
	
}

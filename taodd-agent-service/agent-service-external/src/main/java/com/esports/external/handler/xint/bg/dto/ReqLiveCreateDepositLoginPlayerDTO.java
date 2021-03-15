package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ReqLiveCreateDepositLoginPlayerDTO {
   
	/**
	 * 随机字符串，建议使用UUID
	 */
	private String random;
	
	/**
	 * 签名摘要sign=md5(random+sn+loginId+secretKey)
	 */
	private String digest;
	
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
	private String locale;
	
	/**
	 * 是否返回手机端地址. 1:是; 0:否(默认)
	 */
	private Integer isMobileUrl=0;
	
	/**
	 * 是否返回https地址. 1,是(默认);0,否
	 */
	private Integer isHttpsUrl=0;
	
    @JsonIgnore
    public String getMethod() {
    	return "open.video.game.url";
    }
	
	
	
	
	
	
}

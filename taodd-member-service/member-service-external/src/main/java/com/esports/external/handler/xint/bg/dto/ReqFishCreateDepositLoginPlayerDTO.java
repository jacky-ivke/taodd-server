package com.esports.external.handler.xint.bg.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ReqFishCreateDepositLoginPlayerDTO {
   
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
	private String lang;
	
	/**
	 * 是否返回手机端地址. 1:是; 0:否(默认)
	 */
	private Integer isMobileUrl=0;
	
	/**
	 * 类型,不指定默认BG捕鱼大师
	 */
	private String gameType="105";
	
	
    @JsonIgnore
    public String getMethod() {
    	return "open.game.bg.url";
    }
	
	
	
	
	
	
}

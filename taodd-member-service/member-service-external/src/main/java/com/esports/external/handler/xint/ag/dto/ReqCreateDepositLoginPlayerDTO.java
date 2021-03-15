package com.esports.external.handler.xint.ag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
   
	/**
	 * 代理编码
	 */
	private String cagent;
	
	/**
	 * 登录游戏账号
	 */
	private String loginname;
	
	private String password;
	
	private String dm="";
	
	private String sid;
	
	private Integer actype=0;
	
	private String lang;
	
	private String gameType;
	
	private String oddType="";
	
	private String cur = "CNY";
	
	private String mh5="";
	
	@JsonProperty("session_toaken")
	private String sessionToaken="";
}

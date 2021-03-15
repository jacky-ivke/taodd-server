package com.esports.external.handler.xint.ag.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqPrepareTransferDTO{

	/**
	 * 代理编码
	 */
	private String cagent;
	
	/**
	 * 登录游戏账号
	 */
	private String loginname;
	
	/**
	 * 值= “tc” 代表“预备转账 PrepareTransferCredit”, 是一个常数
	 */
	private String method = "tc";
	
	private String billno;
	
	private String type;
	
	private BigDecimal credit;
	
	private Integer actype = 1;
	
	private String password;
	
	private String fixcredit="";
	
	private Integer gameCategory = 0;
	
	private String cur = "CNY";
	
	
}

package com.esports.external.handler.xint.ag.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO{

	/**
	 * 代理编码
	 */
	private String cagent;
	
	/**
	 * 登录游戏账号
	 */
	private String loginname;
	
	/**
	 * 值= “tcc” 代表“转账确认 TransferCreditComfirm”, 是一个常数
	 */
	private String method = "tcc";
	
	private String billno;
	
	private String type;
	
	private BigDecimal credit;
	
	private Integer actype = 1;
	
	private Integer flag = 1;
	
	private String password;
	
	private String fixcredit;
	
	private Integer gameCategory;
	
	private String cur = "CNY";
	
	
}

package com.esports.external.handler.xint.ag.dto;

import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

	/**
	 * 代理编码
	 */
	private String cagent;
	
	/**
	 * 登录游戏账号
	 */
	private String loginname;
	
	/**
	 * 值 = “gb” 代表”查询余额(GetBalance)”, 是一个常数
	 */
	private String method = "gb";
	
	/**
	 * 1、正式账号   0、试玩账号
	 */
	private Integer actype = 1;
}

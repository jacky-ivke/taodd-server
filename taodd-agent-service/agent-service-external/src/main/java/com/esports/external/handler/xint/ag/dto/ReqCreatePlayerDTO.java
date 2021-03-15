package com.esports.external.handler.xint.ag.dto;

import lombok.Data;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {

	/**
	 * 代理编码
	 */
	private String cagent;
	
	/**
	 * 登录游戏账号
	 */
	private String loginname;
	
	/**
	 * 数值 = “lg” 代表 ”检测并创建游戏账号
	 */
	private String method = "lg";
	
	/**
	 * 1、正式账号   0、试玩账号
	 */
	private Integer actype = 1;
	
	/**
	 * 账号密码
	 */
	private String password;
	
	
	private String oddtype;
	
	/**
	 * 币种类型
	 */
	private String cur = "CNY";
}

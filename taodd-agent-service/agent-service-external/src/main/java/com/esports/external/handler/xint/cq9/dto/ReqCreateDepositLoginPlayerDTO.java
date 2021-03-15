package com.esports.external.handler.xint.cq9.dto;

import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
	
	/**
	 * 玩家账号
	 */
	private String account;
	
	/**
	 * 玩家密码
	 */
	private String password;
}

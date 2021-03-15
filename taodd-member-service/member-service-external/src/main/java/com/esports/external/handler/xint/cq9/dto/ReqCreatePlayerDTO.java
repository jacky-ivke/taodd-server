package com.esports.external.handler.xint.cq9.dto;

import lombok.Data;

/**
 * 创建游戏账号
 * @author jacky
 *
 */
@Data
public class ReqCreatePlayerDTO {

	/**
	 * 玩家账号
	 */
	private String account;
	
	/**
	 * 密码
	 */
	private String password;

	/**
	 * 别名
	 */
	private String nickname;
	
}

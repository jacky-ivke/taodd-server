package com.esports.external.handler.xint.cq9.dto;

import lombok.Data;

@Data
public class ReqOpenGameDTO {

	/**
	 * token
	 */
	private String usertoken;
	
	

	/**
	 * 游戏厂商
	 */
	private String gamehall="CQ9";
	
	/**
	 * 游戏编号
	 */
	private String gamecode="";
	
	private String gameplat="";
	
	private String lang="";
}

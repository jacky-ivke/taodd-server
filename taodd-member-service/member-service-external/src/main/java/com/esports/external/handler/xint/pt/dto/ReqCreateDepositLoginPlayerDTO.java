package com.esports.external.handler.xint.pt.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReqCreateDepositLoginPlayerDTO {
   
	
	private String casinoname="hotspin88am";
	
	private Integer realMode = 1;
	
	private String clientType = "casino";
	
	private String clientPlatform = "web";
	
	private String clientSkin = "hotspin88am";
	
	private String languageCode = "cn";
	
	private String redirectUrl="http://ui.757221.com:1080";
	
	private String requestId = String.valueOf(new Date().getTime() + Math.round(Math.random() * 1000000));
	
	
}

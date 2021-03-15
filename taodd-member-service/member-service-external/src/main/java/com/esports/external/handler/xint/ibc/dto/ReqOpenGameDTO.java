package com.esports.external.handler.xint.ibc.dto;

import lombok.Data;

@Data
public class ReqOpenGameDTO {

	private String token;
	
	private String lang;
	
	private Integer webskintype = 2;
	
	private Integer OType=2;
	
	private String act = "";
	
	private String market = "";
}

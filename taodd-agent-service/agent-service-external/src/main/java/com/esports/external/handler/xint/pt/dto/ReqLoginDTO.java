package com.esports.external.handler.xint.pt.dto;

import lombok.Data;

@Data
public class ReqLoginDTO {

	private String username;
	
	private String password;
	
	private String eventOrigin = "http://ui.757221.com:1080";
	
	private String requestType = "Login";
	
	private String responseType = "json";
}

package com.esports.external.handler.yabo.lottery.dto;

import lombok.Data;

@Data
public class ReqLoginDTO {
	
	private String member;
	
	private String password;
	
	private String merchant;
	
	private Long timestamp;
}

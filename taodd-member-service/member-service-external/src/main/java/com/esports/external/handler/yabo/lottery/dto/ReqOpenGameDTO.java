package com.esports.external.handler.yabo.lottery.dto;

import lombok.Data;

@Data
public class ReqOpenGameDTO {

	private String token;
	
	private String returnUrl = "";
	
	private String lotteryId = "";
}

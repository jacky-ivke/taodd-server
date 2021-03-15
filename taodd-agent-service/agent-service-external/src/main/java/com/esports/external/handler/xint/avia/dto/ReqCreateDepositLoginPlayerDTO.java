package com.esports.external.handler.xint.avia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
	
	@JsonProperty("UserName")
    private String userName;
	
	@JsonProperty("CateID")
    private String cateId="";
	
	@JsonProperty("MatchID")
    private String matchId="";
}

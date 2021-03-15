package com.esports.external.handler.xint.tfg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {
	
	@JsonProperty("LoginName")
	private String loginName;
    
}

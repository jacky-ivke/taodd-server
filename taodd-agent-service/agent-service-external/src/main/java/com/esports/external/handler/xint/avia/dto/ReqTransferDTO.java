package com.esports.external.handler.xint.avia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO {
	
	@JsonProperty("UserName")
    private String userName;
	
	@JsonProperty("Type")
    private String type;
	
	@JsonProperty("Money")
    private BigDecimal money;
	
	@JsonProperty("ID")
    private String id;
	
	@JsonProperty("Currency")
    private String currency;
}

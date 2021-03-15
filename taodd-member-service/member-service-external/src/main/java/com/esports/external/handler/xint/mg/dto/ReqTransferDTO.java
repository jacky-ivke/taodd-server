package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO{
	
	@JsonProperty("account_ext_ref")
	private String loginName;
	
	@JsonProperty("external_ref")
	private String orderNo;
	
    private BigDecimal amount;
    
    private String type;
    
    @JsonProperty("balance_type")
    private String balanceType = "CREDIT_BALANCE";
    
    private String category = "TRANSFER";
}

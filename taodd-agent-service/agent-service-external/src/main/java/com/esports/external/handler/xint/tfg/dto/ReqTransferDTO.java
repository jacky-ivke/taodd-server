package com.esports.external.handler.xint.tfg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO{
	
	@JsonProperty("member")
	private String loginName;
	
	@JsonProperty("operator_id")
	private String operatorId;
	
	@JsonProperty("reference_no")
	private String orderNo;
	
    private BigDecimal amount;
}

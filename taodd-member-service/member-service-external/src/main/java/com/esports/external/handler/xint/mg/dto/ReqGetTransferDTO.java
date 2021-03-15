package com.esports.external.handler.xint.mg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO{

	@JsonProperty("account_ext_ref")
	private String loginName;
	
	@JsonProperty("ext_ref")
	private String orderNo;
}

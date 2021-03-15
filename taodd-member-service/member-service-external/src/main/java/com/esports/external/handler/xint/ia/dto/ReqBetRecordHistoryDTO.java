package com.esports.external.handler.xint.ia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	/**
	 * 密钥
	 */
	@JsonProperty("secret_key")
    private String secretKey;
    
    /**
     * token
     */
	@JsonProperty("operator_token")
    private String operatorToken;
	
	@JsonProperty("row_version")
	private Long versionKey;
	
	@JsonProperty("count")
	private Integer pageSize;
	
	/**
	 * 产品代码
	 */
	@JsonProperty("vendor_code")
	private String vendorCode = "IA";
	
}

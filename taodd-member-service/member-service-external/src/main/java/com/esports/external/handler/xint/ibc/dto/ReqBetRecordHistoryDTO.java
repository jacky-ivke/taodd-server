package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqBetRecordHistoryDTO{
	/**
	 * 厂商标识符
	 */
	@JsonProperty("vendor_id")
    private String vendorId;
	
	/**
	 * 版本号
	 */
	@JsonProperty("version_key")
	private Long versionKey;
}
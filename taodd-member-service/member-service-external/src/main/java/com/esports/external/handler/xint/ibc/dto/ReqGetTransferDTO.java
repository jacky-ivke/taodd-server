package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetTransferDTO {

	/**
	 * 厂商标识符
	 */
	@JsonProperty("vendor_id")
    private String vendorId ;
	
	@JsonProperty("vendor_trans_id")
	private String orderNo;
	
	/**
	 * 产品代码
	 */
	@JsonProperty("wallet_id")
	private Integer walletId;

}

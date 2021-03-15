package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class ReqGetBalanceDTO {

	/**
	 * 厂商标识符
	 */
	@JsonProperty("vendor_id")
    private String vendorId ;
    
    /**
     * 会员账号
     */
	@JsonProperty("vendor_member_ids")
    private String memberId;
	
	/**
	 * 钱包标识
	 */
	@JsonProperty("wallet_id")
	private Integer walletId;
}

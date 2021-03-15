package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqCreateDepositLoginPlayerDTO {
	/**
	 * 厂商标识符
	 */
	@JsonProperty("vendor_id")
    private String vendorId ;
    
    /**
     * 会员账号
     */
	@JsonProperty("vendor_member_id")
    private String memberId;
	
	private String domain="";
}

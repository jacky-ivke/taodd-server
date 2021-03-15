package com.esports.external.handler.xint.ibc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO {

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
	
	/**
	 * 交易号
	 */
	@JsonProperty("vendor_trans_id")
	private String orderNo;
	
	/**
	 * 转账总金额
	 */
	private BigDecimal amount;
	
	/**
	 * 为此会员设置币别
	 */
    private Integer currency = 13;
	
	private Integer direction;
	
	/**
	 * 产品代码
	 */
	@JsonProperty("wallet_id")
	private Integer walletId;
}

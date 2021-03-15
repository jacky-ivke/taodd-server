package com.esports.external.handler.xint.cq9.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class ReqTransferDTO{

	/**
	 * 玩家账号
	 */
	private String account;
	
	/**
	 * 交易代碼
	 */
	private String mtcode;
	
	/**
	 * 金额
	 */
	private BigDecimal amount;
}

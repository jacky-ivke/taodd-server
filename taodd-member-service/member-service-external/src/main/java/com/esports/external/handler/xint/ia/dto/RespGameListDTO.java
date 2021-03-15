package com.esports.external.handler.xint.ia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class RespGameListDTO {

	/**
	 * 下注流水号
	 */
	@JsonProperty("bet_id")
	private String transactionId;
	
	/**
	 * 产品代码
	 */
	@JsonProperty("vendor_code")
	private String vendorCode;
	
	/**
	 * 玩家游戏账号
	 */
	@JsonProperty("player_name")
	private String gameAccount;
	
	/**
	 * 游戏code
	 */
	@JsonProperty("game_code")
	private String gameId;
	
	/**
	 * 钱包代码
	 */
	@JsonProperty("wallet_code")
	private String walletCode;
	

	/**
	 * 订单状态
	 */
	@JsonProperty("trans_type")
	private String okStatus;
	
	/**
	 * 投注币种
	 */
	private String currency;
	
	/**
	 * 派彩金额
	 */
	@JsonProperty("win_amount")
	private BigDecimal profitAmount;

	/**
	 * 有效下注
	 */
	@JsonProperty("bet_amount")
	private BigDecimal betAmount;
	
	/**
	 * 记录创建时间
	 */
	@JsonProperty("created_at")
	private Timestamp createTime;
	
}

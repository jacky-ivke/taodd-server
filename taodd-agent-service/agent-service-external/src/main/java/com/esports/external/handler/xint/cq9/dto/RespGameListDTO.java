package com.esports.external.handler.xint.cq9.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RespGameListDTO {
	
	/**
	 * 下注流水号
	 */
	@JsonProperty("round")
	private String transactionId;
	
	/**
	 * 玩家游戏账号
	 */
	@JsonProperty("account")
	private String gameAccount;
	
	/**
	 * 游戏平台
	 */
	@JsonProperty("gameplat")
	private String platform;
	
	/**
	 * 游戏编号
	 */
	@JsonProperty("gamecode")
	private String gameId;

	/**
	 * 游戏类型
	 */
	@JsonProperty("gametype")
	private String gameType;
	
	/**
	 * 总下注
	 */
	@JsonProperty("bet")
	private BigDecimal betTotal=BigDecimal.ZERO;

	/**
	 * 投注盈亏
	 */
	@JsonProperty("win")
	private BigDecimal profitAmount=BigDecimal.ZERO;
	
	/**
	 * 有效下注
	 */
	@JsonProperty("validbet")
	private BigDecimal betAmount=BigDecimal.ZERO;
	
	/**
	 * 抽水金额
	 */
	@JsonProperty("rake")
	private BigDecimal taxAmount=BigDecimal.ZERO;
	
	/**
	 * 开放费用
	 */
	@JsonProperty("roomfee")
	private BigDecimal roomFee=BigDecimal.ZERO;

	/**
	 * 结算时间
	 */
	@JsonProperty("createtime")
	private String approvalTime;

	/**
	 * 记录创建时间
	 */
	@JsonProperty("bettime")
	private String createTime;
	
	/**
	 * 结束后余额
	 */
	private BigDecimal balance;
	
	/**
	 * 订单状态
	 */
	@JsonProperty("status")
	private String okStatus;
	
	/**
	 * 桌号
	 */
	@JsonProperty("tableid")
	private String tableCode;
}

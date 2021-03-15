package com.esports.rakeback.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class RakebackDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 平台编号
	 */
	private String apiCode;
	
	/**
	 * 游戏类型
	 */
	private String gameType; 
	
	/**
	 * 有效投注
	 */
	private BigDecimal betValidAmount;
	
	/**
	 * 本周洗码量
	 */
	private BigDecimal weekBetAmount;
	
	/**
	 * 返水额
	 */
	private BigDecimal rakeAmount;
	
	/**
	 * 返点比例
	 */
	private BigDecimal percentage;
	
}

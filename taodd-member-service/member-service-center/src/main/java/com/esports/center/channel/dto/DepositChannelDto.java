package com.esports.center.channel.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DepositChannelDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 渠道ID
	 */
	private Long channelId;

	/**
	 * 渠道名称
	 */
	private String channelName;
	
	/**
	 * 支付方式
	 */
	private String payType;
	
	/**
	 * 支付域名
	 */
	private String domain;
	
	/**
	 * 支付图标
	 */
	private String icon;
	
	/**
	 * 最小金额
	 */
	private BigDecimal minAmount;
	
	/**
	 * 最大金额
	 */
	private BigDecimal maxAmount;
}

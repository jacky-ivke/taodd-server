package com.esports.center.scheme.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class DrawSchemeCfgDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * vip等级
	 */
	private Integer vip;

	/**
	 * 是否允许提现
	 */
	private Boolean drawAllow;
	
	/**
	 * 每日提款次数
	 */
	private Integer drawCount;
	
	/**
	 * 每日提款总额
	 */
	private BigDecimal drawTotal;
	
	/**
	 * 提款手续
	 */
	private BigDecimal drawPoint;
	
	/**
	 * 单次提款范围
	 */
	private String drawScope;
	
}

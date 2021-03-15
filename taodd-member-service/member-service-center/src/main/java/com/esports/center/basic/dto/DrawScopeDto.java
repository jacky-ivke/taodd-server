package com.esports.center.basic.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class DrawScopeDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 单笔取款最低额度
	 */
	private BigDecimal minAmount = BigDecimal.ZERO;
	
	/**
	 *单笔取款最高额度
	 */
	private BigDecimal maxAmount = new BigDecimal("10000");
}

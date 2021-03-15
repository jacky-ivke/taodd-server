package com.esports.external.basic.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class PlatformDto implements Serializable{


	private static final long serialVersionUID = 1L;

	/**
	 * 平台编号
	 */
	private String apiCode;
	
	/**
	 * 平台名称
	 */
	private String name;
	
	/**
	 * 余额
	 */
	private BigDecimal balance;
}

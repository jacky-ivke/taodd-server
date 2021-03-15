package com.esports.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class WealthOrderDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 理财套餐
	 */
	private String title;
	
	/**
	 * 是否领取
	 */
	private Boolean receive;

	/**
	 *订单编号
	 */
	private String orderNo;
	
	/**
	 * 理财金额
	 */
	private BigDecimal amount;
	
	/**
	 * 理财日期
	 */
	private Timestamp createTime;
	
	/**
	 * 理财天数
	 */
	private Integer days;
	
	/**
	 * 利率
	 */
	private BigDecimal rate;
	
	/**
	 * 到期收益
	 */
	private BigDecimal profit;
	
	/**
	 * 到期日期
	 */
	private Timestamp approvalTime;
	
	/**
	 * 领取日期
	 */
	private Timestamp receiveTime;
	
	/**
	 * 状态(0、未到期 1、已领取、 2、到期)
	 */
	private Integer okStatus;
}

package com.esports.interest.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 理财收益情况
 * @author jacky
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InterestDto  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 累计收益
	 */
	private BigDecimal interestTotalAmount = BigDecimal.ZERO;
	
	/**
	 * 本月收益
	 */
	private BigDecimal interestMonthAmount = BigDecimal.ZERO;

	/**
	 * 全年收益
	 */
	private BigDecimal interestYearAmount = BigDecimal.ZERO;
	
	/**
	 * 今日收益
	 */
	private BigDecimal interestTodayAmount = BigDecimal.ZERO;
	
	/**
	 * 昨日收益
	 */
	private BigDecimal interestYesterDayAmount = BigDecimal.ZERO;
	
	/**
	 * 本周收益
	 */
	private BigDecimal interestWeekAmount = BigDecimal.ZERO;
}

package com.esports.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DailyReportDto implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
     * 统计日期
     */
    private String createTime;
    
    /**
     * 有效投注
     */
    private BigDecimal betAmount = BigDecimal.ZERO;
    
    /**
     * 投注盈亏
     */
    private BigDecimal profitAmount = BigDecimal.ZERO;
    
    /**
     * 总下注
     */
    private BigDecimal betTotal = BigDecimal.ZERO;
}

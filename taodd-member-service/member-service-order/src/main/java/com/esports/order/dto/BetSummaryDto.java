 package com.esports.order.dto;

 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;

 @Data
 public class BetSummaryDto implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
      * 投注总额
      */
    private BigDecimal betTotalAmount = BigDecimal.ZERO;

    /**
     * 投注总盈亏
     */
    private BigDecimal profitTotalAmount = BigDecimal.ZERO;

    /**
     * 有效投注
     */
    private BigDecimal betAmount =  BigDecimal.ZERO;

 }

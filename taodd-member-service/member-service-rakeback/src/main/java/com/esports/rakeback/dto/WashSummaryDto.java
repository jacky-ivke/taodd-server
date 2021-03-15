 package com.esports.rakeback.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

 @Data
 public class WashSummaryDto implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
      * 有效投注总额
      */
    private BigDecimal betTotalAmount = BigDecimal.ZERO;

    /**
     * 洗码总额
     */
    private BigDecimal rakeTotalAmount = BigDecimal.ZERO;

 }

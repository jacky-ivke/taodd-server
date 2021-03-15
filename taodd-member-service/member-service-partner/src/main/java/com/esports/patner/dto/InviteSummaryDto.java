 package com.esports.patner.dto;

 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;

 @Data
 public class InviteSummaryDto implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
      * 有效投注总额
      */
    private BigDecimal betTotalAmount = BigDecimal.ZERO;

    /**
     * 邀请返佣金额
     */
    private BigDecimal inviteCommissionAmount = BigDecimal.ZERO;

 }

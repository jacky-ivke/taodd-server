 package com.esports.log.dto;

 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 @Data
 public class MemberTradeDto implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
      * 交易时间
      */
     private Timestamp createTime;

     /**
      * 订单编号
      */
     private String orderNo;

     /**
      * 账变类型
      */
     private String type;

     /**
      * 账变金额
      */
     private BigDecimal amount;

     /**
      * 余额
      */
     private BigDecimal balance;

     /**
      * 状态
      */
     private Integer okStatus;

     /**
      * 备注信息
      */
     private String remarks;
 }

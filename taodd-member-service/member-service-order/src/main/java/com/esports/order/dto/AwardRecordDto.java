 package com.esports.order.dto;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import lombok.Data;

 import java.math.BigDecimal;
 import java.sql.Timestamp;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
 public class AwardRecordDto {


     /**
      * 礼金类型
      */
     private String type;

     /**
      *创建时间
      */
     private Timestamp createTime;

     /**
      * 奖励金额
      */
     private BigDecimal amount;

     /**
      * 状态
      */
     private Integer okStatus;

     /**
      * 订单号
      */
     private String orderNo;

 }

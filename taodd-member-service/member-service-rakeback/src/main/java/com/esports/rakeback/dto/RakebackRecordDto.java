 package com.esports.rakeback.dto;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import lombok.Data;

 import java.math.BigDecimal;
 import java.sql.Timestamp;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
 public class RakebackRecordDto {


     /**
      * 平台编号
      */
     private String apiCode;

     /**
      * 游戏类型
      */
     private String gameType;

     /**
      * 订单编号
      */
     private String orderNo;

     /**
      * 有效投注
      */
     private BigDecimal betValidAmount;

     /**
      * 返水额
      */
     private BigDecimal rakeAmount;

     /**
      * 返点比例
      */
     private BigDecimal percentage;

     /**
      * 发放时间
      */
     private Timestamp createTime;
 }

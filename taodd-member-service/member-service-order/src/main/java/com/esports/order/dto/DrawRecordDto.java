 package com.esports.order.dto;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 /**
  * 提款记录
  * @author jacky
  * @date 2020/06/09
  */
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
 public class DrawRecordDto implements Serializable {

      private static final long serialVersionUID = 1L;

     /**
       * 订单编号
       */
      private String orderNo;

      /**
       * 提款日期
       */
      private Timestamp createTime;

      /**
       * 存款金额
       */
      private BigDecimal amount;

      /**
       * 取款账号
       */
      private String bankAccount;

      /**
       * 状态
       */
      private Integer okStatus;
 }

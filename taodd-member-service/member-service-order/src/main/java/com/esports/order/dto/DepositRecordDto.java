 package com.esports.order.dto;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
 public class DepositRecordDto implements Serializable {

      private static final long serialVersionUID = 1L;

      /**
       * 存款类型
       */
      private String depositType="";

     /**
       * 订单编号
       */
      private String orderNo="";

      /**
       * 存款日期
       */
      private Timestamp createTime;

      /**
       * 存款金额
       */
      private BigDecimal amount;

      /**
       * 状态
       */
      private Integer okStatus;

      /**
       * 渠道名称
       */
      private String channalName;
 }

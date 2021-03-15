 package com.esports.order.dto;

 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 /**
  * @author jacky
  * @date 2020/06/05
  */
 @Data
  @ApiModel(value = "drawRecordDto", description = "提款记录")
 public class DrawRecordDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "提款时间", name = "createTime", example = "2020-01-01 12:00:00", required = true, dataType = "String")
      private Timestamp createTime;

      @ApiModelProperty(value = "订单号", name = "orderNo", example = "100000000001", required = true, dataType = "String")
      private String orderNo;

      @ApiModelProperty(value = "提款账号", name = "bankAccount", example = "", required = true, dataType = "String")
      private String bankAccount;

      @ApiModelProperty(value = "状态", name = "okStatus", example = "0、处理中，1、成功， 2、失败", required = true, dataType = "int")
      private Integer okStatus;

      @ApiModelProperty(value = "提款金额", name = "amount", example = "100.00", required = true, dataType = "double")
      private BigDecimal amount;
 }

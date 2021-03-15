 package com.esports.patner.dto;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
  @ApiModel(value = "partnerCommissionDto", description = "超级合伙人,佣金记录对象")
 public class PartnerCommissionDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "发放时间", name = "createTime", example = "2020-05-12 12:00:05", required = false, dataType = "String")
     private Timestamp createTime;

     @ApiModelProperty(value = "佣金金额", name = "amount", example = "2.00", required = false, dataType = "double")
     private BigDecimal amount;

     @ApiModelProperty(value = "订单编号", name = "orderNo", example = "10000000123", required = false, dataType = "String")
     private String orderNo;

 }

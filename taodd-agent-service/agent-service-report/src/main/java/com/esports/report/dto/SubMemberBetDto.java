 package com.esports.report.dto;

 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 /**
   * 代理下线会员投注信息
  */
 @Data
 @ApiModel(value = "subMemberBetDto", description = "代理下线投注对象")
 public class SubMemberBetDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "状态", name = "status", example = "1", required = true, dataType = "Integer")
     private Integer status;

     @ApiModelProperty(value = "会员账号", name = "account", example = "agent118", required = true, dataType = "String")
     private String account;

     @ApiModelProperty(value = "投注单号", name = "orderNo", example = "10000000000123", required = true, dataType = "String")
     private String orderNo;

     @ApiModelProperty(value = "投注平台", name = "platform", example = "BBIN", required = true, dataType = "String")
     private String apiCode;

     @ApiModelProperty(value = "投注内容", name = "gameName", example = "穿越火线", required = true, dataType = "String")
     private String gameName;

     @ApiModelProperty(value = "投注盈亏", name = "profitAmount", example = "10.00", required = true, dataType = "double")
     private BigDecimal profitAmount;

     @ApiModelProperty(value = "投注总额", name = "betTotal", example = "10.00", required = true, dataType = "double")
     private BigDecimal betTotal;

     @ApiModelProperty(value = "有效投注", name = "betAmount", example = "10.00", required = true, dataType = "double")
     private BigDecimal betAmount;

     @ApiModelProperty(value = "下注时间", name = "createTime", example = "2020-01-01 12:10:05", required = true, dataType = "String")
     private Timestamp createTime;
 }

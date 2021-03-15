 package com.esports.transfer.dto;

 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 /**
  * @author jacky
  * @date 2020/06/18
  */
 @Data
 @ApiModel(value = "transferSubMemberDto", description = "代理代存记录对象")
 public class TransferSubMemberDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "会员账号", name = "account", example = "agent118", required = true, dataType = "String")
     private String account;

     @ApiModelProperty(value = "代存金额", name = "amount", example = "100.00", required = true, dataType = "double")
     private BigDecimal amount;

     @ApiModelProperty(value = "出款账户", name = "walletType", example = "100.00", required = true, dataType = "String")
     private String walletType;

     @ApiModelProperty(value = "状态", name = "okStatus", example = "1、成功  2、失败", required = true, dataType = "Integer")
     private Integer okStatus;

     @ApiModelProperty(value = "代存日期", name = "createTime", example = "2020-06-18 00:00:00", required = true, dataType = "String")
     private Timestamp createTime;

     @ApiModelProperty(value = "备注", name = "remarks", example = "备注说明", required = false, dataType = "String")
     private String remarks;
 }

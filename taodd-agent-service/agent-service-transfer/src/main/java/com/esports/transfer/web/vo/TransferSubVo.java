 package com.esports.transfer.web.vo;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
 @ApiModel(value = "transferSubVo", description = "代理提交对象")
 public class TransferSubVo implements Serializable {

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "账户类型", name = "type", example = "commission - 佣金余额   other - 代存余额", required = false, dataType = "String")
     private String type;

     @ApiModelProperty(value = "会员账号", name = "account", example = "wy735289850", required = false, dataType = "String")
     private String account;

     @ApiModelProperty(value = "代存金额", name = "amount", example = "100", required = false, dataType = "Integer")
     private BigDecimal amount;

     @ApiModelProperty(value = "支付密码", name = "password", example = "123456", required = true, dataType = "String")
     private String password;

     @ApiModelProperty(value = "备注", name = "remarks", example = "附言", required = false, dataType = "String")
     private String remarks;

     @ApiModelProperty(value = "来源", name = "source", example = "0", required = true, dataType = "String")
     private Integer source;

 }

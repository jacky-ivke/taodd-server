 package com.esports.agent.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

 /**
  * @author jacky
  * @date 2020/05/29
  */
 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
 @ApiModel(value = "agentDrawVo", description = "代理取款申请对象")
 public class AgentDrawVo implements Serializable {

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "取款人", name = "bankRealName", example = "张飞", required = false, dataType = "String")
     private String bankRealName;

     @ApiModelProperty(value = "银行名称", name = "bankName", example = "中国工商银行", required = false, dataType = "String")
     private String bankName;

     @ApiModelProperty(value = "银行账号", name = "bankAccount", example = "6217009889888876", required = false, dataType = "String")
     private String bankAccount;

     @ApiModelProperty(value = "取款金额", name = "amount", example = "100", required = false, dataType = "Integer")
     private BigDecimal amount;
 }

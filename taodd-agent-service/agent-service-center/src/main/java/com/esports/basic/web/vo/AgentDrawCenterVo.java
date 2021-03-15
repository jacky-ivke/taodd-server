 package com.esports.agent.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

 /**
  * @author jacky
  */
 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
 @ApiModel(value = "agentDrawCenterVo", description = "代理取款申请对象")
 public class AgentDrawCenterVo implements Serializable {

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "备注说明", name = "remarks", example = "测试数据", required = false, dataType = "String")
     private String remarks;

     @ApiModelProperty(value = "取款金额", name = "amount", example = "100", required = false, dataType = "Integer")
     private BigDecimal amount;

     @ApiModelProperty(value = "支付密码", name = "password", example = "123456", required = false, dataType = "String")
     private String password;
 }

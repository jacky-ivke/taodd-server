 package com.esports.basic.dto;

 import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
 import java.sql.Timestamp;

 /**
   * 代理下线会员信息
  * @author jacky
  * @date 2020/05/28
  */
  @Data
  @ApiModel(value = "subMemberDto", description = "代理下线会员对象")
 public class SubMemberDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "会员账号", name = "account", example = "agent118", required = true, dataType = "String")
     private String account;

     @ApiModelProperty(value = "会员姓名", name = "realName", example = "张**", required = true, dataType = "String")
      private String realName;

     @ApiModelProperty(value = "vip等级", name = "vip", example = "1", required = true, dataType = "int")
      private Integer vip;

     @ApiModelProperty(value = "存款", name = "depositAmount", example = "10000.00", required = true, dataType = "double")
      private BigDecimal depositAmount;

     @ApiModelProperty(value = "取款", name = "depositAmount", example = "10000.00", required = true, dataType = "double")
      private BigDecimal drawAmount;

     @ApiModelProperty(value = "总输赢", name = "winAmount", example = "-20", required = true, dataType = "double")
      private BigDecimal profitAmount;

     @ApiModelProperty(value = "最后登录", name = "lastLoginTime", example = "2020-05-10 12:00:00", required = true, dataType = "String")
      private String lastLoginTime;

     @ApiModelProperty(value = "注册时间", name = "regTime", example = "2020-05-10 12:00:00", required = true, dataType = "String")
      private Timestamp regTime;
 }

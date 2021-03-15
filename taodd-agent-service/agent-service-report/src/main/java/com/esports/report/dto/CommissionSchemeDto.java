 package com.esports.report.dto;

 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;

 /**
  * @author jacky
  * @date 2020/06/16
  */
 @Data
 @ApiModel(value = "commissionSchemeDto", description = "返佣方案")
 public class CommissionSchemeDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "API编号", name = "apiCode", example = "BBIN", required = true, dataType = "Integer")
     private String apiCode;

     @ApiModelProperty(value = "游戏类型", name = "gameType", example = "真人、电子", required = true, dataType = "String")
     private String gameType;

     @ApiModelProperty(value = "返佣比例", name = "amount", example = "1.1%", required = true, dataType = "double")
     private BigDecimal percentage;
 }

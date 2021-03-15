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
  @ApiModel(value = "partnerBillDto", description = "超级合伙人,账目统计对象")
 public class PartnerBillDto implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "好友账号", name = "friend", example = "jacky008", required = false, dataType = "String")
     private String friend;

     @ApiModelProperty(value = "有效投注", name = "betAmount", example = "200.00", required = false, dataType = "double")
     private BigDecimal betAmount = BigDecimal.ZERO;

     @ApiModelProperty(value = "好友类型", name = "type", example = "", required = false, dataType = "double")
     private String type;

     @ApiModelProperty(value = "投注日期", name = "createTime", example = "2.00", required = false, dataType = "String")
     private Timestamp createTime;

     @ApiModelProperty(value = "返佣比例", name = "point", example = "0.02", required = false, dataType = "double")
     private BigDecimal point = BigDecimal.ZERO;

     @ApiModelProperty(value = "返佣金额", name = "commissionAmount", example = "10", required = false, dataType = "double")
     private BigDecimal commissionAmount = BigDecimal.ZERO;

 }

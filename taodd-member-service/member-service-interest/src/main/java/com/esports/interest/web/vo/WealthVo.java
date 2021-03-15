 package com.esports.interest.web.vo;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import io.swagger.annotations.ApiModel;
 import lombok.Data;

 import java.io.Serializable;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @ApiModel(value = "wealthVo", description = "领取理财套餐对象")
 @Data
 public class WealthVo implements Serializable{

     private static final long serialVersionUID = 1L;

     private String[] orderNo;
 }

 package com.esports.message.web.vo;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import io.swagger.annotations.ApiModel;
 import io.swagger.annotations.ApiModelProperty;
 import lombok.Data;

 import java.io.Serializable;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
 @ApiModel(value = "noticeVo", description = "消息对象")
 public class NoticeVo implements Serializable{

     private static final long serialVersionUID = 1L;

     @ApiModelProperty(value = "ID集合", name = "ids", example = "1,2,3", required = true, dataType = "String")
     private String[] ids;
 }

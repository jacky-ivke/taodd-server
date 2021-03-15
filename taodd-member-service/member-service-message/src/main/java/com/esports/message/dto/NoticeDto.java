 package com.esports.message.dto;

 import lombok.Data;

 import java.io.Serializable;
 import java.sql.Timestamp;

 @Data
 public class NoticeDto implements Serializable {

     private static final long serialVersionUID = 1L;

     /**
      * ID
      */
     private Long id;

     /**
      * 标题
      */
     private String title;

     /**
      * 时间
      */
     private Timestamp createTime;

     /**
      * 作者
      */
     private String author;

     /**
      * 状态（0、未读 1、已读）
      */
     private Integer status;
 }

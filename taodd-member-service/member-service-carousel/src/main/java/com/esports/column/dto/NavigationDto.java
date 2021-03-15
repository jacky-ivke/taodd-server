 package com.esports.column.dto;

 import lombok.Data;

 import java.io.Serializable;

 /**
  * @author jacky
  * @date 2020/06/30
  */
  @Data
 public class NavigationDto implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
      * 导航编号
      */
     private String code;

     /**
      * 图标
      */
      private String icon;

      /**
       * 提示
       */
      private String tips;

      /**
       * 标题
       */
      private String title;

      /**
       * 是否有二级导航
       */
      private Boolean hasChild;

      /**
       * 关联参数
       */
      private String apiCode;

      /**
       * 关联参数
       */
      private String gameCode;

 }

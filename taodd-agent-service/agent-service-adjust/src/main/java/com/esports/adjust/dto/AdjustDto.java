 package com.esports.adjust.dto;

 import lombok.Data;

 import java.io.Serializable;

  @Data
 public class AdjustDto implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
       * 调线记录数
       */
      private Integer adjustTotal;

      /**
       * 成功调整会员数
       */
      private Integer adjustMember;

      /**
       * 本月调整次数
       */
      private Integer currentMonthAdjust;

 }

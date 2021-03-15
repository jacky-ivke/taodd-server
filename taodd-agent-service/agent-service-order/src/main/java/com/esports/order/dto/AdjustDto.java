 package com.esports.order.dto;

 import lombok.Data;

 import java.io.Serializable;

 /**
  * @author jacky
  * @date 2020/06/15
  */
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

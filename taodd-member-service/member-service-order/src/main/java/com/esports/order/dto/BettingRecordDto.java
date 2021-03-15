 package com.esports.order.dto;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import lombok.Data;

 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @Data
public class BettingRecordDto implements Serializable{

    private static final long serialVersionUID = 1L;

     /**
      * 狀態
      */
    private Integer status;
    
    /**
     * 订单编号
     */
    private String orderNo;

   /**
    * 事务编号
    */
   private String transId;

    /**
      * API编号
      */
     private String apiCode;
     
     /**
      * 游戏类型
      */
     private String gameType;
     
     /**
      * 游戏名称
      */
     private String gameName;
     
     /**
      * 有效投注
      */
     private BigDecimal betAmount = BigDecimal.ZERO;
     
     /**
      * 投注盈亏
      */
     private BigDecimal profitAmount = BigDecimal.ZERO;
     
     /**
      * 总下注
      */
     private BigDecimal betTotal = BigDecimal.ZERO;
     
     /**
      * 下注日期
      */
     private Timestamp createTime;
}

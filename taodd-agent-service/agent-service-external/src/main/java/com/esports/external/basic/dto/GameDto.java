 package com.esports.external.basic.dto;

 import lombok.Data;

 import java.io.Serializable;

  @Data
 public class GameDto implements Serializable {

     private static final long serialVersionUID = 1L;

     /**
       * API编号
       */
      private String apiCode;

      /**
       * 游戏名称
       */
      private String gameName;

      /**
       * 游戏编号
       */
      private String gameCode;

      /**
       * 游戏图标
       */
      private String gameIcon;

      /**
       * 游戏封面
       */
      private String gameCover;

      /** 游戏类型*/
      private String type;
 }

 package com.esports.order.bean.db1;

 import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 import lombok.Data;
 import org.hibernate.annotations.GenericGenerator;

 import javax.persistence.*;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 /**
  * 代存钱包仅用于给下线会员代存，不可取款
  * @author jacky
  * @date 2020/06/18
  */
  @Entity
  @Table(name = "tb_transfer_submember", indexes = { @Index(name = "agent_idx", columnList = "AGENT") })
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
 public class AgentTransferSubOrder implements Serializable{

     private static final long serialVersionUID = 1L;

     /**
       * 主键ID
       */
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @GenericGenerator(name = "paymentableGenerator", strategy = "native")
      @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20,columnDefinition="bigint(20)")
      private Long id;

      /**
       * 订单状态【0、待处理;1、成功;2、失败】
       */
      @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
      private Integer okStatus;

      /**
       * 订单编号
       */
      @Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
      private String orderNo;

      /**
       * 会员账号
       */
      @Column(name = "MEMBER", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
      private String member;

      /**
       * 代理账号
       */
      @Column(name = "AGENT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
      private String agent;

      /**
       * 代存金额
       */
      @Column(name = "AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
      private BigDecimal amount;

      /**
       * 订单来源【移动端、PC端】
       */
      @Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
      private Integer source;

      /**
       * 出款账户类型
       */
      @Column(name = "WALLET_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
      private String walletType;

      /**
       * 创建时间
       */
      @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
      private Timestamp createTime;

      /**
       * 请求IP
       */
      @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="bigint(20)")
      private Long ip;


      /**
       * 备注
       */
      @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
      private String remarks;
 }

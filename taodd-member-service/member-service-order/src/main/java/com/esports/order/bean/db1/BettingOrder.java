package com.esports.order.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_betting_order", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BettingOrder implements Serializable {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 对应平台
     */
    @Column(name = "PLATFORM", unique = false, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String provider;

    /**
     * 游戏transaction id
     */
    @Column(name = "TRANSACTION_ID", unique = true, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String transactionId;

    /**
     * 订单状态【0、待处理（待派奖）  1、成功（已派彩）   2、失败（取消派彩）】
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 订单结果 WIN-赢  LOSS-输  DRAW-和 CANCELLED-取消
     */
    @Column(name = "RESULT", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String result;

    /**
     * 游戏code
     */
    @Column(name = "GAME_ID", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String gameId;

    /**
     * 游戏名称
     */
    @Column(name = "GAME_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
    private String gameName;

    /**
     * 游戏类型
     */
    @JsonProperty("game_type")
    @Column(name = "GAME_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String gameType;

    /**
     * 对应玩家游戏账号
     */
    @Column(name = "GAME_ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String gameAccount;

    /**
     * 玩家账号
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String account;

    /**
     * 总下注
     */
    @Column(name = "BET_TOTAL", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal betTotal;

    /**
     * 盈亏金额
     */
    @Column(name = "PROFIT_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal profitAmount;

    /**
     * 有效下注
     */
    @Column(name = "BET_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal betAmount;

    /**
     * 结算时间
     */
    @JsonProperty("time")
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp approvalTime;

    /**
     * 记录创建时间
     */
    @JsonProperty("created_at")
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 记录更新时间
     */
    @JsonProperty("updated_at")
    @Column(name = "UPDATED_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp updatedTime;

    /**
     * 同步时间
     */
    @Column(name = "SYNC_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp syncTime;
}

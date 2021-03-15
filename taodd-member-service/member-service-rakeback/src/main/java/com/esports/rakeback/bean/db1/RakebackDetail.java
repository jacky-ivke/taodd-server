package com.esports.rakeback.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_rakeback_detail", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT"), @Index(name = "approvaltime_idx", columnList = "APPROVAL_TIME")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RakebackDetail implements Serializable {

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
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 结算批次号
     */
    @Column(name = "SERIAL_NO", unique = false, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String serialNo;

    /**
     * 订单编号【可以用来区分一个批次号里面，用户返水次数，方便数据查看】
     */
    @Column(name = "ORDER_NO", unique = false, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String orderNo;

    /**
     * 平台
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String platform;

    /**
     * 游戏类型
     */
    @Column(name = "GAME_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String gameType;

    /**
     * 投注单数
     */
    @Column(name = "BET_NUM", unique = false, nullable = true, insertable = true, updatable = true, length = 11, columnDefinition = "int(11)")
    private Integer betNum;

    /**
     * 对应玩家
     */
    @Column(name = "ACCOUNT", unique = false, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String account;

    /**
     * 当期有效投注金额
     */
    @Column(name = "BET_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal betAmount;

    /**
     * 盈亏金额
     */
    @Column(name = "PROFIT_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal profitAmount;

    /**
     * 返水比例
     */
    @Column(name = "POINT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal point;

    /**
     * 结算时间【第三方平台的结算时间】
     */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp approvalTime;

    /**
     * 返水发放时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 请求IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long ip;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}

package com.esports.interest.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_wealth_order", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WealthOrder implements Serializable {

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
     * 订单状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 订单编号
     */
    @Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String orderNo;

    /**
     * 理财套餐
     */
    @Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String title;

    /**
     * 订单来源【移动端、PC端】
     */
    @Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer source;

    /**
     * 会员账号
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String account;

    /**
     * 理财金额
     */
    @Column(name = "AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 理财天数
     */
    @Column(name = "DAYS", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer days;

    /**
     * 利率
     */
    @Column(name = "RATE", unique = false, nullable = true, insertable = true, updatable = true, length = 12, columnDefinition = "decimal(12,2)")
    private BigDecimal rate;

    /**
     * 预计收益
     */
    @Column(name = "PROFIT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal profit = BigDecimal.ZERO;

    /**
     * 下单时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 到期时间
     */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp approvalTime;

    /**
     * 是否领取
     */
    @Column(name = "RECEIVE", nullable = true, columnDefinition = "bit default 0  comment '是否领取,False–否, True–是'")
    private Boolean receive = Boolean.FALSE;

    /**
     * 领取时间
     */
    @Column(name = "RECEIVE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp receiveTime;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}

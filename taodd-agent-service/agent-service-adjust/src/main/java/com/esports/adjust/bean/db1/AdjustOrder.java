package com.esports.adjust.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_agent_ajust", indexes = {@Index(name = "agent_idx", columnList = "AGENT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AdjustOrder implements Serializable {

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
     * 订单状态【0、待审;1、成功;2、失败】
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 订单编号
     */
    @Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String orderNo;

    /**
     * 会员账号
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String account;

    /**
     * 代理账号
     */
    @Column(name = "AGENT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String agent;

    /**
     * 调前代理
     */
    @Column(name = "ORIGINAL_AGENT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String originalAgent;

    /**
     * 调前存款
     */
    @Column(name = "DEPOSIT_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal depositAmount;

    /**
     * 调前输赢
     */
    @Column(name = "PROFIT_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal profitAmount;

    /**
     * 调前余额
     */
    @Column(name = "BALANCE", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal balance;

    /**
     * 调前场馆余额
     */
    @Column(name = "API_BALANCE", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal apiBalance;

    /**
     * 首存
     */
    @Column(name = "FIRST_DEPOSIT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal firstDeposit;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String operator;

    /**
     * 审批时间
     */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp approvalTime;

    /**
     * 引导链接
     */
    @Column(name = "GUIDE_LINK", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String guideLink;

    /**
     * 订单来源【移动端、PC端】
     */
    @Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer source;

    /**
     * 终端类型
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer type;

    /**
     * 请求IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long ip;


    /**
     * 图片材料（图片数量有限，采用extjson模式 存储）
     */
    @Column(name = "IMGS", unique = false, nullable = true, insertable = true, updatable = true, length = 0, columnDefinition = "text")
    private String imgs;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}

package com.esports.order.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_deposit_order", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT"),
	    @Index(name = "orderno_idx", columnList = "ORDER_NO")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel("存款订单记录对象")
public class DepositOrder implements Serializable {

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
     * 订单状态【0、待支付;1、已支付;2、取消】
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 订单编号
     */
    @Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String orderNo;

    /**
     * 账户类型
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String type;

    /**
     * 收款账号 |   商户号
     */
    @Column(name = "BANK_ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 19, columnDefinition = "varchar(19)")
    private String bankAccount;

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
     * 存款金额
     */
    @Column(name = "AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal amount;

    /**
     * 存款手续费
     */
    @Column(name = "POINT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal point;

    /**
     * 实际入账金额
     */
    @Column(name = "ACTUAL_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal actualAmount;

    /**
     * 随机金额【辅组自动入账程序使用】
     */
    @Column(name = "RANDOM_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal randomAmount;

    /**
     * 渠道名称
     */
    @Column(name = "CHANNEL_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
    private String channelName;

    /**
     * 账户类型【online、线上支付 company、公司入款】
     */
    @Column(name = "PAY_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String payType;

    /**
     * 请求IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long ip;

    /**
     * 下单时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 审批时间
     */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp approvalTime;
    
    /**
     * 玩家身份【'member'会员、'agent'代理】
     */
    @Column(name = "IDENTITY", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
    private String identity;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}

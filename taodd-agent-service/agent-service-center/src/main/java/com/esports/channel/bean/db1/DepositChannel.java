package com.esports.channel.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_deposit_channel")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DepositChannel implements Serializable {

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
	 * 状态
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
	private Integer okStatus = 1;

	/**
	 * 渠道类型【'alipay':** 'wechat'：** 'qq':**,预留字段】
	 */
	@Column(name = "CHANNEL_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
	private String channelType;
	/**
	 * 账户类型【online、线上支付 company、公司入款】
	 */
	@Column(name = "PAY_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String payType;

	/**
	 * 账户名称
	 */
	@Column(name = "PAY_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
	private String payName;

	/**
	 * 优先级
	 */
	@Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
	private Integer priority;

	/**
	 * 图标
	 */
	@Column(name = "ICON", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
	private String icon;

	/**
	 * 渠道名称
	 */
	@Column(name = "CHANNEL_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
	private String channelName;

	/**
	 * 最小存款金额
	 */
	@Column(name = "MIN_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
	private BigDecimal minAmount = BigDecimal.ZERO;

	/**
	 * 最大存款金额
	 */
	@Column(name = "MAX_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
	private BigDecimal maxAmount = BigDecimal.ZERO;

	/**
	 * 存款手续费
	 */
	@Column(name = "DEPOSIT_POINT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
	private BigDecimal depositPoint = BigDecimal.ZERO;

	/**
	 * 收款账号 | 商户号
	 */
	@Column(name = "BANK_ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 19, columnDefinition = "varchar(19)")
	private String bankAccount;

	/**
	 * 收款户名 | 商户密钥
	 */
	@Column(name = "BANK_REALNAME", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
	private String bankRealName;

	/**
	 * 支付域名
	 */
	@Column(name = "DOMAIN", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
	private String domain;

	/**
	 * 收款二维码
	 */
	@Column(name = "QRCODE", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
	private String qrcode;

	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
	private Timestamp createTime;

	/**
	 * 备注
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
	private String remarks;
}

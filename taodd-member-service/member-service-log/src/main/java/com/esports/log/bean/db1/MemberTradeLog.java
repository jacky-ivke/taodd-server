package com.esports.log.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_trade_log", indexes = {
    @Index(name = "account_idx", columnList = "ACCOUNT"),
	@Index(name = "secondtype_idx", columnList = "SECOND_TYPE")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MemberTradeLog implements Serializable {

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
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long id;
	
	/**
	 * 账单状态
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 2,columnDefinition="int(2)")
	private Integer okStatus;
	
	/**
	 * 账单类型【如：存款、取款、奖励】
	 */
	@Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length =20,columnDefinition="varchar(20)")
	private String type;
	
	/**
	 * 具体类型【如：人工存款、公司入款等】
	 */
	@Column(name = "SECOND_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length =20,columnDefinition="varchar(20)")
	private String secondType;
	
	/**
	 * 账单金额
	 */
	@Column(name = "AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal amount;
	
	/**
	 * 账单编号
	 */
	@Column(name = "ORDER_NO", unique = false, nullable = false, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
	private String orderNo;
	
	/**
	 * 会员账号
	 */
	@Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String account;
	
	/**
	 * 交易后账户余额
	 */
	@Column(name = "BALANCE",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal balance;
	
	/**
	 * 账单时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
	
	/**
	 * IP地址
	 */
	@Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long ip;
	
	/**
	 * 备注
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
	private String remarks;
	
	/**
	 * 操作者
	 */
	@Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String operator;

	/**
	 * 附言信息
	 */
	@Column(name = "POSTSCRIPT", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
	private String postscript;
}

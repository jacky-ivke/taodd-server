package com.esports.patner.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_partner_order", indexes = { @Index(name = "account_idx", columnList = "ACCOUNT") })
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PartnerOrder implements Serializable{

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
	 * 订单状态【0、处理中、1、成功、2、失败】
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
	private Integer okStatus;

	/**
	 * 订单编号
	 */
	@Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
	private String orderNo;
	
	/**
	 * 投注事务ID
	 */
	@Column(name = "TRANSACTION_ID", unique = false, nullable = true, insertable = true, updatable = true, length = 128 , columnDefinition = "varchar(128)")
	private String transactionId;

	/**
	 * 订单来源【移动端、PC端】
	 */
	@Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
	private Integer source;

	/**
	 * 玩家账号【会员】
	 */
	@Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String account;
	
	/**
	 * 好友账号
	 */
	@Column(name = "FRIEND", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String friend;
	
	/**
	 * 佣金类型【first 一级返佣 second 二级返佣 third 三级返佣】
	 */
	@Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String type;
	   
	/**
	 * 好友有效投注
	 */
	@Column(name = "BET_AMOUNT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
	private BigDecimal betAmount;
	
	/**
	 * 投注总额
	 */
	@Column(name = "BET_TOTAL", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
	private BigDecimal betTotal;
	
	/**
	 * 返点百分比
	 */
	@Column(name = "PERCENTAGE", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
	private BigDecimal percentage;

	/**
	 * 下单时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
	private Timestamp createTime;
	
	/**
	 * 备注
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
	private String remarks;

}

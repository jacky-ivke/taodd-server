package com.esports.external.basic.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_transfer_order")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TransferOrder implements Serializable{
	
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
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20,columnDefinition="bigint(20)")
	private Long id;
    
    /**
     * 订单状态【1、成功  2、失败 】
     */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
    private Integer okStatus;
    
	/**
	 * 交易类型【deposit 或 withdraw】
	 */
	@Column(name = "TYPE", unique = false, nullable = false, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String type;
	
	/**
	 * 订单编号
	 */
	@Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
    private String orderNo;
    
    /**
     * 订单来源【移动端、PC端】
     */
	@Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
    private Integer source;
    
    /**
     * 会员账号
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
    private String account;
    
    /**
     * 支出账号
     */
    @Column(name = "EXPENDITURE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
    private String expenditure;
    
    /**
     * 收入账号
     */
    @Column(name = "REVENUE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
    private String revenue;
    
    /**
     * 转账金额
     */
    @Column(name = "AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
    private BigDecimal amount;
    
    /**
     * 操作完成后平台余额
     */
    @Column(name = "BALANCE",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
    private BigDecimal balance;
    
    /**
	 * 下单时间
	 */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
    
    /**
     * 请求IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="bigint(20)")
	private Long ip;
    
    /**
     * 转账平台生成的事务ID
     */
    @Column(name = "TRANSACTION_ID", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
    private String transactionId;
	
   /**
    * 备注
    */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
    private String remarks;
}

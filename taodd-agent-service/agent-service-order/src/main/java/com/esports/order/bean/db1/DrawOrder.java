package com.esports.order.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_draw_order", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT"),
	    @Index(name = "orderno_idx", columnList = "ORDER_NO")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DrawOrder implements Serializable{
	
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
     * 订单状态【0、处理中、1、成功、2、失败】
     */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
    private Integer okStatus;
	
	/**
	 * 【0、待审核、1、待出库、2、审核失败、3、驳回 、4、出款成功】
	 */
	@Column(name = "AUDIT_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
    private Integer auditStatus;
	
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
     * 玩家账号【会员或者代理】
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
    private String account;
    
    /**
     * 真实姓名
     */
    @Column(name = "BANK_REALNAME", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
    private String bankRealName;
    
    /**
     * 银行名称
     */
    @Column(name = "BANK_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
    private String bankName;
    
    /**
     * 银行卡号
     */
    @Column(name = "BANK_ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 19,columnDefinition="varchar(19)")
    private String bankAccount;
    
    /**
     * 提款金额
     */
    @Column(name = "AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
    private BigDecimal amount;
    
    /**
     * 提款手续费
     */
    @Column(name = "COST_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
    private BigDecimal costAmount;
    
    /**
	 * 下单时间
	 */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
	
	/**
	 * 审批时间
	 */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp approvalTime;
    
	/**
	 * 是否转账【true-已转，false-未转】
	 */
    @Column(name = "TRANSFER", nullable = true, columnDefinition = "bit default 0  comment '是否转账,False–否, True–是'")
    private Boolean transfer = Boolean.FALSE;
	
	/**
	 * 出款转账时间
	 */
	@Column(name = "TRANSFER_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp transferTime;
    
    /**
     * 请求IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="bigint(20)")
	private Long ip;
    
    /**
	 * 玩家身份【'member'会员、'agent'代理】
	 */
	@Column(name = "IDENTITY", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String identity;
	
   /**
    * 备注
    */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
    private String remarks;
}

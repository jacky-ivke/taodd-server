package com.esports.rakeback.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_rakeback_order", indexes = { @Index(name = "account_idx", columnList = "ACCOUNT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RakebackOrder implements Serializable {

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
    * 订单状态【2、拒绝结算;1、确认结算】
     */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
    private Integer okStatus;
	
	/**
	 * 结算批次号
	 */
	@Column(name = "SERIAL_NO", unique = false, nullable = false, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
    private String serialNo;
	
	/**
	 * 订单编号【可以用来区分一个批次号里面，用户返水次数，方便数据查看】
	 */
	@Column(name = "ORDER_NO", unique = true, nullable = false, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
	private String orderNo;
	
	/**
	 * 对应玩家
	 */
	@Column(name = "ACCOUNT", unique = false, nullable = false, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String account;
	
	/**
	 * 本期有效投注单数
	 */
	@Column(name = "BET_NUM", unique = false, nullable = true, insertable = true, updatable = true, length = 11,columnDefinition="int(11)")
	private Integer betNum;
	
	/**
	 * 本期有效投注金额
	 */
	@Column(name = "BET_VALID_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal betValidAmount;
	
	/**
	 * 投注赢取金额
	 */
	@Column(name = "PROFIT_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal profitAmount;


	/**
	 * 本期应付返水总额
	 */
	@Column(name = "RAKE_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal rakeAmount;
	
	/**
	 * 本期实付返水总额
	 */
	@Column(name = "ACTUAL_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal actualAmount;
	
	/**
	 * 返水发放时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
	
	/**
     * 请求IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="bigint(20)")
	private Long ip;
    
   /**
    * 备注
    */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
    private String remarks;
}

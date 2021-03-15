package com.esports.interest.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_wealth")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Wealth implements Serializable{
	
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
	
	@Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
	private String title;
	
	/**
	 * 理财天数
	 */
	@Column(name = "DAYS", unique = false, nullable = true, insertable = true, updatable = true, length = 4,columnDefinition="int(4)")
	private Integer days;
	
	/**
	 * 利率
	 */
	@Column(name = "RATE",unique = false, nullable = true, insertable = true, updatable = true, length = 12,columnDefinition = "decimal(12,2)")
	private BigDecimal rate;

	/**
	 * 状态
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
	private Integer okStatus = 1;
	
	/**
	 * 备注说明
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
	private String remarks;
	
	/**
	 * 优先级
	 */
	@Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4,columnDefinition="int(4)")
	private Integer priority;
	
	/**
	 * 稽核倍数
	 */
	@Column(name = "AUDIT_MULTIPLE",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal auditMultiple = new BigDecimal("1");
	
	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
}
	
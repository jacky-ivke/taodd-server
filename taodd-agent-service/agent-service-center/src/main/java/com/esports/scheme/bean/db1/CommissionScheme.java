package com.esports.scheme.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_commission_scheme")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CommissionScheme implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "paymentableGenerator", strategy = "native")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long id;
	
	/**
	 * 状态
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,columnDefinition="int(1)")
	private Integer okStatus;
	
	/**
	 * 方案代号
	 */
	@Column(name = "SCHEME_CODE", unique = true, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String schemeCode;
	
	/**
	 * 方案名称
	 */
	@Column(name = "SCHEME_NAME", unique = true, nullable = true, insertable = true, updatable = true, length = 64,columnDefinition="varchar(64)")
	private String schemeName;
	
	/**
	 * 存款手续费用
	 */
	@Column(name = "DEPOSIT_POINT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal depositPoint;	
	
	/**
	 * 取款手续费用
	 */
	@Column(name = "DRAW_POINT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal drawPoint;	
	
	/**
	 * 取款下限
	 */
	@Column(name = "MIN_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal minAmount;
	
	/**
	 * 取款上限
	 */
	@Column(name = "MAX_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal maxAmount;
	
	/**
	 * 方案配置
	 */
	@Column(name = "SCHEME_CONFIG", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition="text")
	private String schemeConfig;
	
	
	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
}

package com.esports.external.basic.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
* @ClassName: ExternalApp 
* @Description: 外部应用平台（预留）
* @Author: jacky
* @Version: V1.0
 */
@Entity
@Table(name = "tb_app_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AppType implements Serializable {
	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID,自增长
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "paymentableGenerator", strategy = "native")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long id;

	/**
	 * 应用状态
	 */
	@Column(name = "IS_USED", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "bit")
	private Boolean isUsed = Boolean.TRUE;
	
	/**
	 * 类型名称
	 */
	@Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
	private String title;
	
	/**
	 * 编号
	 */
	@Column(name = "GAME_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
	private String gameType;

	/**
	 * API编号
	 */
	@Column(name = "API_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
	private String apiCode;

	/**
	 * 默认平台费
	 */
	@Column(name = "DEFAULT_FEE", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal defaultFee = BigDecimal.ZERO;

	/**
	 * 默认反水比列
	 */
	@Column(name = "DEFAULT_RAKE", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal defaultRake;

	/**
	 * 默认代理返佣
	 */
	@Column(name = "DEFAULT_COMMISSION", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal defaultCommission;

}
	
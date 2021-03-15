package com.esports.scheme.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 返水方案配置明细
 * @author jacky
 *
 */
@Entity
@Table(name = "tb_commission_cfg")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CommissionCfg implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "paymentableGenerator", strategy = "native")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long id;
	
	/**
	 * 方案代号
	 */
	@Column(name = "SCHEME_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String schemeCode;
	
	/**
	 * 方案名称
	 */
	@Column(name = "SCHEME_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 64,columnDefinition="varchar(64)")
	private String schemeName;
	
	/**
	 * 返水平台编号
	 */
	@Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 600,columnDefinition="varchar(600)")
	private String platform;
	
	/**
	 * 游戏类型
	 */
	@Column(name = "GAME_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 600,columnDefinition="varchar(600)")
	private String gameType;
	
	/**
	 * 返水分摊比例
	 */
	@Column(name = "RAKE_POINT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal rakePoint;
	
	/**
	 * 优惠分摊比例
	 */
	@Column(name = "DISCOUNT_POINT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal discountPoint;
	
	/**
	 * 其它分摊比例
	 */
	@Column(name = "OTHER_POINT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal otherPoint;
	
	/**
	 * 游戏类别返佣比例
	 */
	@Column(name = "COMMISSION_POINT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal commissionPoint;
	
	/**
	 * 每期返佣上限
	 */
	@Column(name = "UPPER_LIMIT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal upperLimit;
	
	/**
	 * 盈利总额
	 */
	@Column(name = "PROFIT_AMOUNT",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal profitAmount;
	
	/**
	 * 有效会员数
	 */
	@Column(name = "MEMBER_NUM", unique = false, nullable = true, insertable = true, updatable = true, length = 2,columnDefinition="int(2)")
	private Integer memberNum;
	
	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
}

package com.esports.center.vip.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 会员VIP等级晋升规则
 * @author jacky
 *
 */
@Entity
@Table(name = "tb_vip_rule")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VipRule implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "paymentableGenerator", strategy = "native")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long id;
	
	/**
	 * 等级
	 */
	@Column(name = "GRADE", unique = false, nullable = true, insertable = true, updatable = true, length = 2,columnDefinition="int(2)")
	private Integer grade;
	
	/**
	 * 称号
	 */
	@Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String title;
	
	/**
	 * 累计存款
	 */
	@Column(name = "DEPOSIT_TOTAL",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal depositTotal = BigDecimal.ZERO;
	
	/**
	 * 有效投注量
	 */
	@Column(name = "BET_TOTAL",unique = false, nullable = true, insertable = true, updatable = true,columnDefinition = "decimal(12,2)")
	private BigDecimal betTotal = BigDecimal.ZERO;
	
	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
	
}

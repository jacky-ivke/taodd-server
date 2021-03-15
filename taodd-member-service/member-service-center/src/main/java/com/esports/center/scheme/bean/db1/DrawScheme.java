package com.esports.center.scheme.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_draw_scheme")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DrawScheme implements Serializable {

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
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
	private Long id;

	/**
	 * 状态
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
	private Integer okStatus;

	/**
	 * 方案代号
	 */
	@Column(name = "SCHEME_CODE", unique = true, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String schemeCode;

	/**
	 * 方案名称
	 */
	@Column(name = "SCHEME_NAME", unique = true, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
	private String schemeName;

	/**
	 * 方案配置
	 */
	@Column(name = "SCHEME_CONFIG", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "text")
	private String schemeConfig;

	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
	private Timestamp createTime;

	/**
	 * 备注说明
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
	private String remarks;
}

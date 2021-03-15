package com.esports.center.release.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Table(name = "tb_activity")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Activity implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "paymentableGenerator", strategy = "native")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
	private Long id;

	/**
	 * 活动状态
	 */
	@Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
	private Integer okStatus;

	/**
	 * 活动标题
	 */
	@Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
	private String title;

	/**
	 * 子标题
	 */
	@Column(name = "SUB_TEXT", unique = false, nullable = true, insertable = true, updatable = true, length = 800, columnDefinition = "varchar(800)")
	private String subText;

	/**
	 * 活动类型【真人优惠、电子优惠、特别优惠、长期优惠、棋牌优惠】
	 */
	@Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String type;

	/**
	 * 活动属性类别[首充、首体、充值送等]
	 */
	@Column(name = "ATTR", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
	private String attr;

	/**
	 * 发布终端【0、PC 1、移动端】
	 */
	@Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
	private String platform;

	/**
	 * 优先级
	 */
	@Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
	private Integer priority;

	/**
	 * 开始时间
	 */
	@Column(name = "START_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
	private Timestamp startTime;

	/**
	 * 结束时间
	 */
	@Column(name = "END_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
	private Timestamp endTime;

	/**
	 * 是否自动派发活动金额【true-自动发放，false-人工审核】
	 */
	@Column(name = "AUTO_AWARD", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "bit default 0")
	private Boolean autoAward = Boolean.FALSE;

	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
	private Timestamp createTime;

	/**
	 * 活动图片
	 */
	@Column(name = "PC_URL", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(255)")
	private String pcUrl;

	/**
	 * 活动图片
	 */
	@Column(name = "H5_URL", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(255)")
	private String h5Url;

	/**
	 * 活动说明
	 */
	@Column(name = "DESCRIPTION", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
	private String description;
}

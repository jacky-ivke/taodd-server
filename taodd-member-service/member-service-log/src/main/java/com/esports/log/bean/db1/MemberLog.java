package com.esports.log.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_member_log", indexes = {
    @Index(name = "account_idx", columnList = "ACCOUNT")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MemberLog implements Serializable{

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
	 * 操作对象
	 */
	@Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String account;
	
	/**
	 * 变动类型
	 */
	@Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
	private String type;
	
	/**
	 *来源
	 */
	@Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
	private String source;
	
	/**
	 * 目标
	 */
	@Column(name = "TARGET", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
	private String target;
	
	/**
	 * 操作IP
	 */
	@Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long ip;
	
	/**
	 * 创建时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
	
	/**
	 * 备注信息
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 500,columnDefinition="varchar(500)")
	private String remarks;
}

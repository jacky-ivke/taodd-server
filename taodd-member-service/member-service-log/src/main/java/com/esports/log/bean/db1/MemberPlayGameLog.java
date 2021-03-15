package com.esports.log.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_opengame_log", indexes = {
    @Index(name = "account_idx", columnList = "ACCOUNT")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MemberPlayGameLog implements Serializable {

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
     * 应用平台编号
     */
    @Column(name = "APP_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String appCode;
    
    /**
     * 游戏编号
     */
    @Column(name = "GAME_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
    private String gameCode;
    
	/**
	 * 会员账号
	 */
	@Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,columnDefinition="varchar(20)")
	private String account;
	
	/**
	 * 创建时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;
	
	/**
	 * IP地址
	 */
	@Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 11,columnDefinition="bigint(20)")
	private Long ip;
}

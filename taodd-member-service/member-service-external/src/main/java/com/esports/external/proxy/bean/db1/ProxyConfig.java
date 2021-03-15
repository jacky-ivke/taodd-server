package com.esports.external.proxy.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_proxy_config")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ProxyConfig implements Serializable {

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
	 * 商户类型：（YABO, XT, GM2）
	 */
	@Column(name = "MERCHANT_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 16,columnDefinition="varchar(16)")
	private String merchantType;

	/**
	 * 商户编号
	 */
	@Column(name = "MERCHANT_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 255,columnDefinition="varchar(255)")
	@ColumnTransformer(read = "convert(aes_decrypt(unhex(merchant_code), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))" )
	private String merchantCode;

	/**
	 * YABOZR、YABOCP、AG、BG等
	 */
	@Column(name = "API_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
	private String apiCode;

	/**
	 * 域名
	 */
	@Column(name = "DOMAIN", unique = false, nullable = true, insertable = true, updatable = true, length = 255,columnDefinition="varchar(255)")
	@ColumnTransformer(read = "convert(aes_decrypt(unhex(domain), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))" )
	private String domain;

	/**
	 * 接口地址
	 */
	@Column(name = "API_URL", unique = false, nullable = true, insertable = true, updatable = true, length = 255,columnDefinition="varchar(255)")
	@ColumnTransformer(read = "convert(aes_decrypt(unhex(api_url), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))" )
	private String apiUrl;

	/**
	 * 接口类型
	 */
	@Column(name = "API_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,columnDefinition="varchar(32)")
	private String apiType;

	/**
	 * 密钥
	 */
	@Column(name = "AES_KEY", unique = false, nullable = true, insertable = true, updatable = true, length = 1000,columnDefinition="varchar(1000)")
	@ColumnTransformer(read = "convert(aes_decrypt(unhex(aes_key), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))" )
	private String aesKey;

	/**
	 * MD5KEY/偏移量
	 */
	@Column(name = "MD5_KEY", unique = false, nullable = true, insertable = true, updatable = true, length = 1000,columnDefinition="varchar(1000)")
	@ColumnTransformer(read = "convert(aes_decrypt(unhex(md5_key), 'aoying') using utf8)", write = "hex(aes_encrypt(?, 'aoying'))" )
	private String md5key;

	/**
	 * 备注
	 */
	@Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,columnDefinition="varchar(200)")
	private String remarks;

	/**
	 * 更新时间
	 */
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,columnDefinition="datetime")
	private Timestamp createTime;

	@JsonIgnore
	public String getRquestUrl() {
		String reqUrl = this.domain + this.apiUrl;
		return reqUrl;
	}
}

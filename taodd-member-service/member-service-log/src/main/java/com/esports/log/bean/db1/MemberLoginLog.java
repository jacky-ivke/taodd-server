package com.esports.log.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_login_log", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MemberLoginLog implements Serializable {

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
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11,
        columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 会员账号
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
        columnDefinition = "varchar(20)")
    private String account;

    /**
     * 登录时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,
        columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 登录平台
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
        columnDefinition = "int(1)")
    private Integer platform;

    /**
     * 登录IP
     */
    @Column(name = "IP", unique = false, nullable = true, insertable = true, updatable = true, length = 11,
        columnDefinition = "bigint(20)")
    private Long ip;

    /**
     * 注册IP区域
     */
    @Column(name = "AREA", unique = false, nullable = true, insertable = true, updatable = true, length = 128,columnDefinition="varchar(128)")
    private String area;

    /**
     * 客户端版本
     */
    @Column(name = "CLIENT", unique = false, nullable = true, insertable = true, updatable = true, length = 500,
        columnDefinition = "varchar(500)")
    private String client;
}

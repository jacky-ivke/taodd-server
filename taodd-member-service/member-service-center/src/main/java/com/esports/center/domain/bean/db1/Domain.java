package com.esports.center.domain.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_domain")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Domain implements Serializable {

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
     * 域名名称
     */
    @Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 128, columnDefinition = "varchar(128)")
    private String title;

    /**
     * 域名指向类型【main、主页, deposit、支付域名】
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String type;

    /**
     * 域名指向
     */
    @Column(name = "URL", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String url;


    /**
     * 设置默认
     */
    @Column(name = "SET_DEFAULT", nullable = true, columnDefinition = "bit default 0  comment 'False–否, True–是'")
    private Boolean setDefault = Boolean.FALSE;


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

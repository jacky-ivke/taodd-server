package com.esports.center.audit.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_agent_audit")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 事务ID
     */
    @Column(name = "TRANSACTION_ID", unique = false, nullable = false, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String transactionId;


    @Column(name = "LOGIN_PWD", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String loginPwd;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 账号
     */
    @Column(name = "ACCOUNT", unique = true, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String account;


    @Column(name = "REAL_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String realName;

    /**
     * 手机号
     */
    @Column(name = "MOBILE", unique = false, nullable = true, insertable = true, updatable = true, length = 15, columnDefinition = "varchar(15)")
    private String mobile;

    /**
     * 邮箱
     */
    @Column(name = "EMAIL", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String email;

    /**
     * 订单来源【移动端、PC端】
     */
    @Column(name = "SOURCE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer source;

    /**
     * 申请时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 申请IP
     */
    @Column(name = "IP", unique = false, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
    private Long ip;


    @Column(name = "AREA", unique = false, nullable = true, insertable = true, updatable = true, length = 128, columnDefinition = "varchar(128)")
    private String area;

    /**
     * 所属代理
     */
    @Column(name = "LEADER", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "varchar(20)")
    private String leader;

    /**
     * 总代
     */
    @Column(name = "TOP_LEADER", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "varchar(20)")
    private String topLeader;

    /**
     * 审核时间
     */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp approvalTime;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "varchar(20)")
    private String operator;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}

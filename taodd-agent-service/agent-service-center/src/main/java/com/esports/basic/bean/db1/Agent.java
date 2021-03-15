package com.esports.basic.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @ClassName: Player
 * @Description: 玩家管理（代理、总代只是玩家的另一重身份）
 * @Author: jacky
 * @Version: V1.0
 */
@Entity
@Table(name = "tb_player", indexes = {@Index(name = "account_idx", columnList = "ACCOUNT"),
        @Index(name = "mobile_idx", columnList = "MOBILE")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Agent implements Serializable {

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20,
            columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 玩家层级代号
     */
    @Column(name = "GRADE_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String gradeCode;

    /**
     * 预设下级玩家层级代号
     */
    @Column(name = "SUB_GRADE_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String subGradeCode;

    /**
     * 返佣方案代号
     */
    @Column(name = "COMMISSION_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String commissionCode;

    /**
     * VIP
     */
    @Column(name = "VIP", unique = false, nullable = true, insertable = true, updatable = true, length = 2,
            columnDefinition = "int(2)")
    private Integer vip = 0;

    /**
     * 玩家账号
     */
    @Column(name = "ACCOUNT", unique = true, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String account;

    /**
     * GM2代理时间（在GM2平台注册的时间）
     */
    @Column(name = "GM2_TIME", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "datetime")
    private Timestamp gm2Time;

    /**
     * 邀请码
     */
    @Column(name = "INVITE_CODE", unique = true, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String inviteCode;

    /**
     * 邀请人
     */
    @Column(name = "INVITER", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String inviter;

    /**
     * 真实姓名
     */
    @Column(name = "REAL_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 16,
            columnDefinition = "varchar(16)")
    private String realName;

    /**
     * 玩家类型【1、正式、0、模拟】
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
            columnDefinition = "int(1)")
    private Integer type;

    /**
     * 注册时间
     */
    @Column(name = "REG_TIME", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "datetime")
    private Timestamp regTime;

    /**
     * 手机号
     */
    @Column(name = "MOBILE", unique = false, nullable = true, insertable = true, updatable = true, length = 15,
            columnDefinition = "varchar(15)")
    private String mobile;

    /**
     * 风控标签
     */
    @Column(name = "RISK_TAG", unique = false, nullable = true, insertable = true, updatable = true, length = 512,
            columnDefinition = "varchar(512)")
    private String riskTag;

    /**
     * 专属链接-h5
     */
    @Column(name = "H5_EXCLUSIVE_LINKS", unique = false, nullable = true, insertable = true, updatable = true,
            length = 512, columnDefinition = "varchar(512)")
    private String h5exclusiveLinks;

    /**
     * 专属链接-PC
     */
    @Column(name = "PC_EXCLUSIVE_LINKS", unique = false, nullable = true, insertable = true, updatable = true,
            length = 512, columnDefinition = "varchar(512)")
    private String pcexclusiveLinks;

    /**
     * 邮箱
     */
    @Column(name = "EMAIL", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String email;

    /**
     * 微信号
     */
    @Column(name = "WECHAT", unique = true, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String wechat;

    /**
     * QQ
     */
    @Column(name = "QQ", unique = true, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String qq;

    /**
     * 出生日期
     */
    @Column(name = "BIRTHDAY", unique = true, nullable = true, insertable = true, updatable = true, length = 10,
            columnDefinition = "varchar(10)")
    private String birthday;

    /**
     * 登录密码
     */
    @Column(name = "LOGIN_PWD", unique = false, nullable = true, insertable = true, updatable = true, length = 64,
            columnDefinition = "varchar(64)")
    private String loginPwd;

    /**
     * 交易密码
     */
    @Column(name = "TRADE_PWD", unique = false, nullable = true, insertable = true, updatable = true, length = 64,
            columnDefinition = "varchar(64)")
    private String tradePwd;

    /**
     * 注册平台【1、PC 2、移动端】
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
            columnDefinition = "int(1)")
    private Integer platform;

    /**
     * 注册IP
     */
    @Column(name = "IP", unique = false, nullable = false, insertable = true, updatable = true, length = 11,
            columnDefinition = "bigint(20)")
    private Long ip;

    /**
     * 注册IP区域
     */
    @Column(name = "AREA", unique = false, nullable = true, insertable = true, updatable = true, length = 128,
            columnDefinition = "varchar(128)")
    private String area;

    /**
     * 头像
     */
    @Column(name = "HEAD_PORTRAIT", unique = false, nullable = true, insertable = true, updatable = true, length = 64,
            columnDefinition = "varchar(255)")
    private String headPortrait;

    /**
     * 账号是否锁定
     */
    @Column(name = "LOCKED", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "bit(1) default 1")
    private Boolean locked;

    /**
     * 会员状态(预留扩展)
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
            columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 账户余额
     */
    @Column(name = "BALANCE", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "decimal(12,2)")
    private BigDecimal balance;

    /**
     * 利息余额
     */
    @Column(name = "INTEREST_BALANCE", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "decimal(12,2)")
    private BigDecimal interestBalance;

    /**
     * 推荐返佣
     */
    @Column(name = "INVITE_BALANCE", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "decimal(12,2)")
    private BigDecimal inviteBalance;

    /**
     * 代理佣金
     */
    @Column(name = "COMMISSION_BALANCE", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "decimal(12,2)")
    private BigDecimal commissionBalance;

    /**
     * 代存钱包
     */
    @Column(name = "OTHER_BALANCE", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "decimal(12,2)")
    private BigDecimal otherBalance;

    /**
     * 是否可提款
     */
    @Column(name = "IS_DRAW", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "bit(1)")
    private Boolean isDraw = Boolean.FALSE;

    /**
     * 所属代理
     */
    @Column(name = "LEADER", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "varchar(20)")
    private String leader;

    /**
     * 玩家身份【'member'会员、'agent'代理' 、'top' 总代 】
     */
    @Column(name = "IDENTITY", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String identity;

    /**
     * 总代
     */
    @Column(name = "TOP_LEADER", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "varchar(20)")
    private String topLeader;

    /**
     * 审核时间
     */
    @Column(name = "APPROVAL_TIME", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "datetime")
    private Timestamp approvalTime;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "varchar(20)")
    private String operator;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,
            columnDefinition = "varchar(200)")
    private String remarks;
}

package com.esports.consumber.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_customer_config")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerExecutive implements Serializable {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 客服编号
     */
    @Column(name = "JOB_NUMBER", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String jobNumber;

    /**
     * 优先级
     */
    @Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer priority;

    /**
     * 手机号
     */
    @Column(name = "MOBILE", unique = false, nullable = true, insertable = true, updatable = true, length = 15,
            columnDefinition = "varchar(15)")
    private String mobile;

    /**
     * QQ
     */
    @Column(name = "QQ", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String qq;

    /**
     * 邮箱
     */
    @Column(name = "EMAIL", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String email;

    /**
     * TELEGRAM
     */
    @Column(name = "TELEGRAM", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String telegram;

    /**
     * SKYPE
     */
    @Column(name = "SKYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String skype;

    /**
     * FLYGRAM
     */
    @Column(name = "FLYGRAM", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String flygram;

    /**
     * 在线地址
     */
    @Column(name = "ONLINE_URL", unique = false, nullable = true, insertable = true, updatable = true, length = 255,
            columnDefinition = "varchar(255)")
    private String onlineUrl;

    /**
     * 客服分类
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String type;

    /**
     * 微信号
     */
    @Column(name = "WECHAT", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String wechat;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;
}

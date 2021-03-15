package com.esports.consumber.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName: Activity
 * @Description: 咨询问题管理
 * @Author: jacky
 * @Version: V1.0
 */
@Entity
@Table(name = "tb_customer_questions")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerQuestions implements Serializable {

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
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 玩家账号
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20,
            columnDefinition = "varchar(20)")
    private String account;

    /**
     * 咨询内容
     */
    @Column(name = "CONTENT", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String content;

    /**
     * 手机号
     */
    @Column(name = "MOBILE", unique = false, nullable = true, insertable = true, updatable = true, length = 15,
            columnDefinition = "varchar(15)")
    private String mobile;

    /**
     * 创建时间a
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp updateTime;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String operator;
}

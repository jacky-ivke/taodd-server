package com.esports.message.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_sys_message", uniqueConstraints = {@UniqueConstraint(columnNames = {"MESSAGE_ID", "ACCOUNT"})}, indexes = {@Index(name = "account_idx", columnList = "ACCOUNT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data

public class SysMessage implements Serializable {

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
     * 消息ID
     */
    @Column(name = "MESSAGE_ID", unique = false, nullable = false, insertable = true, updatable = true, length = 20,
            columnDefinition = "bigint(20)")
    private Long messageId;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,
            columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 接收人
     */
    @Column(name = "ACCOUNT", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String account;

    /**
     * 消息状态(预留，后期可能会涉及已读、未读、删除操作)
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
            columnDefinition = "int(1)")
    private Integer okStatus;
}

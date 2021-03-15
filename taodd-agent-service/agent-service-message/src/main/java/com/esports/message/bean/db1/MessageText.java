package com.esports.message.bean.db1;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Table(name = "tb_message_text")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MessageText implements Serializable {

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
     * 标题
     */
    @Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 128, columnDefinition = "varchar(128)")
    private String title;

    /**
     * 消息类型
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String type;

    /**
     * 发布终端
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String platform;

    /**
     * 消息对象
     */
    @Column(name = "IDENTITY", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String identity;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 消息内容
     */
    @Column(name = "CONTENT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "text")
    private String content;

    /**
     * 操作者
     */
    @Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String operator;
}

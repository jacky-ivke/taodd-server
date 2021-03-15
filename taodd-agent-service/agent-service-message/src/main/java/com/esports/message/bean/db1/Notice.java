package com.esports.message.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Table(name = "tb_notice")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Notice implements Serializable {

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
     * 类型(预留字段)
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer type;

    /**
     * 发布终端
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String platform;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 内容
     */
    @Column(name = "CONTENT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "longtext")
    private String content;

    /**
     * 链接类型(预留字段)
     */
    @Column(name = "LINK_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer linkType;

    /**
     * 作者
     */
    @Column(name = "AUTHOR", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String author;

    /**
     * 优先级
     */
    @Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer priority;

    /**
     * 是否滚动
     */
    @Column(name = "IS_ROLL", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "bit")
    private Boolean roll;

    /**
     * 是否弹出
     */
    @Column(name = "IS_POP", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "bit")
    private Boolean pop;

    /**
     * 开始时间
     */
    @Column(name = "START_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp startTime;

    /**
     * 结束时间
     */
    @Column(name = "END_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp endTime;

    /**
     * 公告发布对象【'member'会员、'agent'代理】
     */
    @Column(name = "IDENTITY", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String identity;

    /**
     * 接受账号
     */
    @Column(name = "ACCOUNTS", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "text")
    private String accounts;

    /**
     * 指定VIP等级
     */
    @Column(name = "VIPS", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "text")
    private String vips;

    /**
     * 指定层级
     */
    @Column(name = "GRADES", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "text")
    private String grades;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR", unique = false, nullable = true, insertable = true, updatable = true, length = 16, columnDefinition = "varchar(16)")
    private String operator;
}

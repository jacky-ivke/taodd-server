package com.esports.carousel.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Table(name = "tb_banner")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Banner implements Serializable {
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
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 标题
     */
    @Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 50, columnDefinition = "varchar(50)")
    private String title;

    /**
     * 导航编号
     */
    @Column(name = "NAV_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String navCode;

    /**
     * 优先级
     */
    @Column(name = "PRORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer prority;

    /**
     * 发布终端
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String platform;

    /**
     * 图片地址
     */
    @Column(name = "URL", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String url;

    /**
     * 状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 链接
     */
    @Column(name = "link", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String link;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;
}

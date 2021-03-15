package com.esports.external.basic.bean.db1;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 
 * @ClassName: ExternalApp
 * @Description: 外部应用平台
 * @Author: jacky
 * @Version: V1.0
 */
@Entity
@Table(name = "tb_external_app")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExternalApp implements Serializable {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID,自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20,
        columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 应用平台标题
     */
    @Column(name = "TITLE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
        columnDefinition = "varchar(32)")
    private String title;

    /**
     * 应用平台编号
     */
    @Column(name = "APP_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
        columnDefinition = "varchar(32)")
    private String appCode;

    /**
     * 应用ID
     */
    @Column(name = "APP_ID", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
        columnDefinition = "varchar(32)")
    private String appId;

    /**
     * 平台余额
     */
    @Column(name = "BALANCE", columnDefinition = "decimal(12,2)")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 应用图标
     */
    @Column(name = "APP_ICON", unique = false, nullable = true, insertable = true, updatable = true, length = 255,
        columnDefinition = "varchar(255)")
    private String appIcon;

    /**
     * 应用状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1,
        columnDefinition = "int(1)")
    private Integer okStatus = 1;

    /**
     * 备注说明
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200,
        columnDefinition = "varchar(200)")
    private String remarks;

    /**
     * 包含游戏类型
     */
    @Column(name = "GAME_TYPES", unique = false, nullable = true, insertable = true, updatable = true, length = 200,
        columnDefinition = "varchar(200)")
    private String gameTypes;

    /**
     * 优先级
     */
    @Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4,
        columnDefinition = "int(4)")
    private Integer priority;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true,
        columnDefinition = "datetime")
    private Timestamp createTime;
}

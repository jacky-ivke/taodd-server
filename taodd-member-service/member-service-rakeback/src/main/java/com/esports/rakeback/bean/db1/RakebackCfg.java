package com.esports.rakeback.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 返水方案配置明细
 *
 * @author jacky
 */
@Entity
@Table(name = "tb_rakeback_cfg")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RakebackCfg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 方案代号
     */
    @Column(name = "SCHEME_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String schemeCode;

    /**
     * 方案名称
     */
    @Column(name = "SCHEME_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String schemeName;

    /**
     * 返水平台编号
     */
    @Column(name = "PLATFORM", unique = false, nullable = true, insertable = true, updatable = true, length = 600, columnDefinition = "varchar(600)")
    private String platform;

    /**
     * 游戏类型
     */
    @Column(name = "GAME_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 600, columnDefinition = "varchar(600)")
    private String gameType;

    /**
     * 返水比列
     */
    @Column(name = "POINT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal point;

    /**
     * VIP等级
     */
    @Column(name = "VIP", unique = false, nullable = true, insertable = true, updatable = true, length = 2, columnDefinition = "int(2)")
    private Integer vip;

    /**
     * 每期返水上限
     */
    @Column(name = "UPPER_LIMIT", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "decimal(12,2)")
    private BigDecimal upperLimit;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;
}

package com.esports.external.basic.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName: ExternalGame
 * @Description:第三方游戏管理
 * @Author: jacky
 * @Version: V1.0
 */
@Entity
@Table(name = "tb_external_game")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ExternalGame implements Serializable {

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
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 20, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 游戏名称
     */
    @Column(name = "GAME_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String gameName;

    /**
     * 游戏编号
     */
    @Column(name = "GAME_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String gameCode;

    /**
     * 支持设备
     */
    @Column(name = "SUPPORT", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String support;

    /**
     * 应用图标
     */
    @Column(name = "GAME_ICON", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String gameIcon;

    /**
     * 封面图
     */
    @Column(name = "GAME_COVER", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String gameCover;

    /**
     * 所属分类
     */
    @Column(name = "TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String type;

    /**
     * 游戏标签（hot:热门,recommend:比玩推荐）
     */
    @Column(name = "TAG", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String tag;

    /**
     * 游戏状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus = 1;

    /**
     * 备注说明
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;

    /**
     * 优先级
     */
    @Column(name = "PRIORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer priority;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * API编号
     */
    @Column(name = "API_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String apiCode;
}
	
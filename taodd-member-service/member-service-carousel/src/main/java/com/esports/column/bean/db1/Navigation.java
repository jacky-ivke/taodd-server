package com.esports.column.bean.db1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "tb_navigation")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"children"})
@Getter
@Setter
public class Navigation implements Serializable {

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
     * 导航名称
     */
    @Column(name = "NAV_NAME", unique = false, nullable = true, insertable = true, updatable = true, length = 20, columnDefinition = "varchar(20)")
    private String navName;

    /**
     * 唯一编号
     */
    @Column(name = "NAV_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32, columnDefinition = "varchar(32)")
    private String navCode;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 是否需要授权登录
     */
    @Column(name = "IS_LOGIN", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "bit(1) default 0")
    private Boolean isLogin;

    /**
     * 父导航
     */
    @ManyToOne
    @LazyCollection(LazyCollectionOption.EXTRA)
    @JoinColumn(name = "PARENT_ID")
    private Navigation parent;

    /**
     * 优先级
     */
    @Column(name = "PRORITY", unique = false, nullable = true, insertable = true, updatable = true, length = 4, columnDefinition = "int(4)")
    private Integer prority;

    /**
     * 备注
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 300, columnDefinition = "varchar(300)")
    private String remarks;

    /**
     * 导航图标
     */
    @Column(name = "NAV_ICON", unique = false, nullable = true, insertable = true, updatable = true, length = 255, columnDefinition = "varchar(255)")
    private String navIcon;

    /**
     * 导航状态
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus;

    /**
     * 属性提示
     */
    @Column(name = "TIP_TYPE", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer tipType;

    /**
     * 子导航
     */
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL})
    @OrderBy("PRORITY ASC, CREATE_TIME DESC")
    private List<Navigation> children;

    /**
     * API编号
     */
    @Column(name = "API_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String apiCode;

    /**
     * 电脑大厅
     */
    @Column(name = "PC_GAME_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String pcGameCode;

    /**
     * 手机大厅
     */
    @Column(name = "H5_GAME_CODE", unique = false, nullable = true, insertable = true, updatable = true, length = 32,
            columnDefinition = "varchar(32)")
    private String h5GameCode;
}

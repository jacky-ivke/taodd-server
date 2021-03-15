package com.esports.center.scheme.bean.db1;

import com.esports.center.channel.bean.db1.DepositChannel;
import com.esports.center.release.bean.db1.Activity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "tb_grade_scheme")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class GradeScheme implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "paymentableGenerator", strategy = "native")
    @Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true, length = 11, columnDefinition = "bigint(20)")
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
     * 默认开启
     */
    @Column(name = "OK_STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 1, columnDefinition = "int(1)")
    private Integer okStatus = 1;

    /**
     * 更新时间
     */
    @Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, columnDefinition = "datetime")
    private Timestamp createTime;

    /**
     * 风控标签
     */
    @Column(name = "RISK_TAG", unique = false, nullable = true, insertable = true, updatable = true, length = 512,
            columnDefinition = "varchar(512)")
    private String riskTag;

    /**
     * 危险标记
     */
    @Column(name = "DANGER_SIGNS", unique = false, nullable = true, insertable = true, updatable = true, length = 64, columnDefinition = "varchar(64)")
    private String dangerSigns;

    /**
     * 设置默认
     */
    @Column(name = "SET_DEFAULT", nullable = true, columnDefinition = "bit default 0  comment 'False–否, True–是'")
    private Boolean setDefault = Boolean.FALSE;

    /**
     * 备注说明
     */
    @Column(name = "REMARKS", unique = false, nullable = true, insertable = true, updatable = true, length = 200, columnDefinition = "varchar(200)")
    private String remarks;

    /**
     * 关联活动
     */
    @ManyToMany(targetEntity = Activity.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_grade_activity", joinColumns = {@JoinColumn(name = "GRADE_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))}, inverseJoinColumns = {
            @JoinColumn(name = "ACTIVITY_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))})
    private Set<Activity> activitys;

    /**
     * 关联存款渠道
     */
    @ManyToMany(targetEntity = DepositChannel.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_grade_deposit", joinColumns = {
            @JoinColumn(name = "GRADE_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))}, inverseJoinColumns = {
            @JoinColumn(name = "DEPOSIT_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))})
    private Set<DepositChannel> channels;

    /**
     * 提款方案
     */
    @ManyToOne(targetEntity = DrawScheme.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.DETACH}, fetch = FetchType.LAZY)
//	@JoinColumn(name = "DRAW_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @JoinTable(name = "tb_grade_draw", joinColumns = {
            @JoinColumn(name = "GRADE_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))}, inverseJoinColumns = {
            @JoinColumn(name = "DRAW_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))})
    private DrawScheme drawScheme;

    /**
     * 返水方案
     */
    @ManyToOne(targetEntity = RakebackScheme.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.DETACH}, fetch = FetchType.LAZY)
//	@JoinColumn(name = "RAKE_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @JoinTable(name = "tb_grade_rake", joinColumns = {
            @JoinColumn(name = "GRADE_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))}, inverseJoinColumns = {
            @JoinColumn(name = "RAKE_ID", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))})
    private RakebackScheme rakeScheme;
}

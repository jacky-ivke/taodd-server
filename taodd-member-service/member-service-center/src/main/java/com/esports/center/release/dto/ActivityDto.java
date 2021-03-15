package com.esports.center.release.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActivityDto implements Serializable {

    /**
     * 活动名称
     */
    private String name;

    /**
     * 获取时间范围
     */
    private String timeRange;

    /**
     * 详情地址
     */
    private String linkUrl;

    /**
     * 活动封面
     */
    private String icon;
}

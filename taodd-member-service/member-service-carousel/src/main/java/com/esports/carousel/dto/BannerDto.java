package com.esports.carousel.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BannerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 跳转
     */
    private String link;
}

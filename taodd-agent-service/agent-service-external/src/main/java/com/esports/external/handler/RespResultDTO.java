package com.esports.external.handler;

import lombok.Data;

@Data
public class RespResultDTO {

    /**
     * 响应结果
     */
    private Boolean success;

    /**
     * 响应状态
     */
    private String code;

    /**
     * 接口响应信息描述
     */
    private String message;

    /**
     * 接口响应处理数据
     */
    private Object data;

    /**
     * 接口响应原始数据
     */
    private String source;
}

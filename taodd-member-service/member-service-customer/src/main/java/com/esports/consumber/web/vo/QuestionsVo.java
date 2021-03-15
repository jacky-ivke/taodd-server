package com.esports.consumber.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "QuestionsVo", description = "咨询对象")
@Data
public class QuestionsVo implements Serializable {

    @ApiModelProperty(value = "手机号码", name = "mobile", example = "15100090909", required = true, dataType = "String")
    private String mobile;

    @ApiModelProperty(value = "咨询内容", name = "question", example = "xxxxxxx", required = false, dataType = "String")
    private String question;
}

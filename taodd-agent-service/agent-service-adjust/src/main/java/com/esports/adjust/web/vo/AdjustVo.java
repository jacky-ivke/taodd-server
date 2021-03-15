package com.esports.adjust.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "adjustVo", description = "代理调线申请对象")
public class AdjustVo {

    @ApiModelProperty(value = "备注说明", name = "remarks", example = "测试数据", required = false, dataType = "String")
    private String remarks;

    @ApiModelProperty(value = "会员账号", name = "account", example = "langzz", required = false, dataType = "String")
    private String account;
    
    @ApiModelProperty(value = "引导链接", name = "guideLink", example = "http://45.197.97.146:8080?inviteCode=", required = false, dataType = "String")
    private String guideLink;
    
    @ApiModelProperty(value = "终端类型", name = "type", example = "0、PC 1、H5 2、ios  3、Android", required = false, dataType = "Integer")
    private Integer type;
    
    @ApiModelProperty(value = "订单来源", name = "type", example = "0、PC 1、H5", required = false, dataType = "Integer")
    private Integer source;
    
    @ApiModelProperty(value = "图片数组", name = "imgs", example = "", required = false, dataType = "String")
    private List<String> imgs;
    
    

}

package com.esports.external.handler.xint.bg.dto;

import lombok.Data;
import net.sf.json.JSONObject;

import java.util.UUID;

@Data
public class RequestParamDTO {

    private String id = UUID.randomUUID().toString().replaceAll("-","");

    private String method;

    private JSONObject params;
    
    private String jsonrpc = "2.0";
}

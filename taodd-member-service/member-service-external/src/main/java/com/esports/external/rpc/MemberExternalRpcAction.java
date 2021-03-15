package com.esports.external.rpc;

import com.esports.external.basic.service.ExternalGameManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"【会员管理系统-第三方应用接口提供者】"})
@RestController
public class MemberExternalRpcAction {

    @Autowired
    private ExternalGameManager externalGameManager;

    @RequestMapping(value = "/external/getGameName", method = RequestMethod.GET)
    public String getGameName(String apiCode, String gameCode){
        String gameName = externalGameManager.getGameZhName(apiCode, gameCode);
        return gameName;
    }

}

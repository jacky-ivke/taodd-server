package com.esports.api.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface MemberExternalService {

    /**
     * 获取游戏名称
     * @return
     */
    @RequestMapping(value = "/external/getGameName", method = RequestMethod.GET)
    String getGameName(@RequestParam("apiCode") String apiCode,@RequestParam("gameCode") String gameCode);
}

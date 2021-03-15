package com.esports.api.message;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface MemberMessgeService {

    /**
     * 获取玩家未读消息
     *
     * @return
     */
    @RequestMapping(value = "/message/getUnreadMsg", method = RequestMethod.GET)
    String getUnreadMsg(@RequestParam("account") String account,
                        @RequestParam("source") Integer source,
                        @RequestParam("identity") String identity,
                        @RequestParam("grade") String grade,
                        @RequestParam("vip") Integer vip);

}

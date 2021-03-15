package com.esports.api.interest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface MemberInterestService {


    /**
     * 自动发放已到期可领取的理财订单
     */
    @RequestMapping(value = "/interest/autoReceive", method = RequestMethod.POST)
    String autoReceive(@RequestParam("account") String account, @RequestParam("ip") String ip);
}


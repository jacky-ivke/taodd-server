package com.esports.api.message;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "taodd-deploy", path = "/v1.0")
public interface AgentMessageService {

    /**
     * 发送站内信息
     */
    @RequestMapping(value = "/message/saveEventMessage", method = RequestMethod.POST)
    void saveEventMessage(@RequestParam("title") String title,
                          @RequestParam("messageText") String messageText,
                          @RequestParam("receve") String receve,
                          @RequestParam("identity") String identity,
                          @RequestParam("platform") String platform);

    /**
     * 获取玩家未读消息
     *
     * @return
     */
    @RequestMapping(value = "/message/getUnreadMsg", method = RequestMethod.GET)
    String getUnreadMsg(@RequestParam("account") String account, @RequestParam("source") Integer source, @RequestParam("identity") String identity);
}

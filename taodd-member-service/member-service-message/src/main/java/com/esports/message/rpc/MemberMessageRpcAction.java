package com.esports.message.rpc;

import com.esports.message.dto.UnReadMsgDto;
import com.esports.message.service.NoticeManager;
import com.esports.utils.JsonUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"【会员管理系统-站内信、公告接口提供者】"})
@RestController
public class MemberMessageRpcAction {

    @Autowired
    private NoticeManager noticeManager;

    @RequestMapping(value = "/message/getUnreadMsg", method = RequestMethod.GET)
    String getUnreadMsg(String account, Integer source, String identity, String grade, Integer vip){

        UnReadMsgDto dto = noticeManager.getUnreadMsg(source, account, identity, grade, vip);
        if(null == dto){
            return null;
        }
        String json = JsonUtil.object2String(dto);
        return json;
    }
}


package com.esports.message.rpc;

import com.esports.message.dto.UnReadMsgDto;
import com.esports.message.service.NoticeManager;
import com.esports.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"【代理管理系统-站内信相关接口提供者】"})
@RestController
public class AgentMessageRpcAction {

    @Autowired
    private NoticeManager noticeManager;

    @ApiOperation("内部模块依赖暴露接口【发送站内信息】")
    @RequestMapping(value = "/message/saveEventMessage", method = RequestMethod.POST)
    public void saveEventMessage(String title, String messageText, String receve, String identity, String platform) {
        noticeManager.saveEventMessage(title,messageText, receve, identity, platform);
    }

    @ApiOperation("内部模块依赖暴露接口【获取代理未读信息】")
    @RequestMapping(value = "/message/getUnreadMsg", method = RequestMethod.GET)
    String getUnreadMsg(String account, Integer source, String identity){

        UnReadMsgDto dto = noticeManager.getUnreadMsg(source, account, identity);
        if(null == dto){
            return null;
        }
        String json = JsonUtil.object2String(dto);
        return json;
    }
}

package com.esports.message.service;

import com.esports.constant.MessageCode;
import com.esports.message.bean.db1.SysMessage;
import com.esports.message.dao.db1.SysMessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

@Service
public class SysMessgeManager {

    @Autowired
    private SysMessageDao sysMessageDao;

    public Integer checkMessageStatus(Long id, String receve) {
        Integer messageStatus = MessageCode._STATUS_UNREAD.getCode();
        if (null == id || StringUtils.isEmpty(receve)) {
            return messageStatus;
        }
        SysMessage sysMessage = sysMessageDao.findByMessageIdAndAccount(id, receve);
        if (null == sysMessage) {
            return messageStatus;
        }
        messageStatus = sysMessage.getOkStatus();
        return messageStatus;
    }

    public void deleteMessage(Long[] ids, String receve) {
        if (null == ids) {
            return;
        }
        for (Long messageId : ids) {
            SysMessage sysMessage = sysMessageDao.findByMessageIdAndAccount(messageId, receve);
            if (null != sysMessage) {
                sysMessageDao.updateMessageStatus(messageId, MessageCode._STATUS_DELETE.getCode(), receve);
            } else {
                SysMessage sysMsg = new SysMessage();
                sysMsg.setMessageId(messageId);
                sysMsg.setAccount(receve);
                sysMsg.setOkStatus(MessageCode._STATUS_DELETE.getCode());
                sysMsg.setCreateTime(new Timestamp(System.currentTimeMillis()));
                sysMessageDao.save(sysMsg);
            }
        }
    }

    public void sendMessage(Long messageId, String receve) {
        SysMessage sysMessage = sysMessageDao.findByMessageIdAndAccount(messageId, receve);
        if (null != sysMessage) {
            return;
        }
        SysMessage sysMsg = new SysMessage();
        sysMsg.setMessageId(messageId);
        sysMsg.setAccount(receve);
        sysMsg.setOkStatus(MessageCode._STATUS_READ.getCode());
        sysMsg.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sysMessageDao.save(sysMsg);
    }
}

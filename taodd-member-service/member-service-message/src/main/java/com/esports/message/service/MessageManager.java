package com.esports.message.service;

import com.esports.message.bean.db1.MessageText;
import com.esports.message.dao.db1.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageManager {

    @Autowired
    private MessageDao messageDao;

    public MessageText getMessageText(String type, String identity, Integer source) {
        MessageText msgText = messageDao.getMessageText(type, identity, source);
        return msgText;
    }
}

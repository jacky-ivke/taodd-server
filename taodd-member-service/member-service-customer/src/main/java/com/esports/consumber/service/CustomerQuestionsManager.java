package com.esports.consumber.service;

import com.esports.constant.OrderCode;
import com.esports.consumber.bean.db1.CustomerQuestions;
import com.esports.consumber.dao.db1.CustomerQuestionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class CustomerQuestionsManager {

    @Autowired
    private CustomerQuestionsDao customerQuestionsDao;

    public void saveQuestion (CustomerQuestions questions) {
        if(null == questions){
            return;
        }
        questions.setCreateTime(new Timestamp(System.currentTimeMillis()));
        questions.setOkStatus(OrderCode._PENDING.getCode());
        customerQuestionsDao.save(questions);
    }
}

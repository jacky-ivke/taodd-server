package com.esports.consumber.dao.db1;

import com.esports.consumber.bean.db1.CustomerQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerQuestionsDao extends JpaRepository<CustomerQuestions, Long>, JpaSpecificationExecutor<CustomerQuestions> {
}

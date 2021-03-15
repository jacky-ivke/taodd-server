package com.esports.bankcard.dao.db1;

import com.esports.bankcard.bean.db1.AgentBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentBankCardDao extends JpaRepository<AgentBankCard,Long>{
	
	AgentBankCard findByBankAccountAndOkStatus(String bankAccount, Integer okStatus);

	List<AgentBankCard> findByAccountAndOkStatus(String account, Integer okStatus);
}

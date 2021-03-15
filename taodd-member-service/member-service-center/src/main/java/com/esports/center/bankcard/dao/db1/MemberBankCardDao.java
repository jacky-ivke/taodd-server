package com.esports.center.bankcard.dao.db1;

import com.esports.center.bankcard.bean.db1.MemberBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBankCardDao extends JpaRepository<MemberBankCard,Long>{
	
	MemberBankCard findByBankAccountAndOkStatus(String bankAccount, Integer okStatus);

	List<MemberBankCard> findByAccountAndOkStatus(String account, Integer okStatus);
}

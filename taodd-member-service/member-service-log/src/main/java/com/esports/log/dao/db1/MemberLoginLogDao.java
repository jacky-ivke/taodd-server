package com.esports.log.dao.db1;

import com.esports.log.bean.db1.MemberLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface MemberLoginLogDao extends JpaRepository<MemberLoginLog, Long>, JpaSpecificationExecutor<MemberLoginLog>{

	@Query(value=""
			+ "select "
			+"		max(create_time) loginTime" 
			+" from " 
			+"		tb_login_log"
			+ " where account=?1",nativeQuery=true)
	Timestamp getLastLoginTime(String account);
}

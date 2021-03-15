package com.esports.log.dao.db1;

import com.esports.log.bean.db1.AgentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentLogDao extends JpaRepository<AgentLog, Long>, JpaSpecificationExecutor<AgentLog>{

}

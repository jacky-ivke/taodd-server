 package com.esports.audit.dao.db1;

 import com.esports.audit.bean.db1.AgentAudit;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 @Repository
 public interface AgentAuditDao extends JpaRepository<AgentAudit,Long>,JpaSpecificationExecutor<AgentAudit>{


   List<AgentAudit> findByAccountAndOkStatus(String account, Integer okStatus);

   List<AgentAudit> findByMobileAndOkStatus(String account, Integer okStatus);
 }

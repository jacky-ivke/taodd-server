package com.esports.transfer.dao.db1;

import com.esports.transfer.bean.db1.AgentTransferSubOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentTransferSubDao extends JpaRepository<AgentTransferSubOrder, Long>, JpaSpecificationExecutor<AgentTransferSubOrder> {
}

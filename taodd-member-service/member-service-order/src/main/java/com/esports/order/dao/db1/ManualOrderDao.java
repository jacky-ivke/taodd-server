package com.esports.order.dao.db1;

import com.esports.order.bean.db1.ManualOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualOrderDao extends JpaRepository<ManualOrder, Long>, JpaSpecificationExecutor<ManualOrder> {
}

package com.esports.consumber.dao.db1;

import com.esports.consumber.bean.db1.CustomerExecutive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerExecutiveDao extends JpaRepository<CustomerExecutive, Long>, JpaSpecificationExecutor<CustomerExecutive> {


}

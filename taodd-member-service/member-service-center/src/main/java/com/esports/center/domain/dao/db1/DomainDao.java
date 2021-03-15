 package com.esports.center.domain.dao.db1;

 import com.esports.center.domain.bean.db1.Domain;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
 import org.springframework.stereotype.Repository;

 @Repository
 public interface DomainDao extends JpaRepository<Domain,Long>,JpaSpecificationExecutor<Domain>{


     Domain findByTypeAndSetDefault(String type, Boolean setDefault);
 }

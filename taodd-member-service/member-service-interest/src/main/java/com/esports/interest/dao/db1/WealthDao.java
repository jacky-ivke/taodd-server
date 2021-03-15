package com.esports.interest.dao.db1;

import com.esports.interest.bean.db1.Wealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WealthDao extends JpaRepository<Wealth, Long>, JpaSpecificationExecutor<Wealth> {
}

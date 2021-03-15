package com.esports.center.scheme.dao.db1;

import com.esports.center.scheme.bean.db1.DepositScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositSchemeDao extends JpaRepository<DepositScheme,Long> {
}

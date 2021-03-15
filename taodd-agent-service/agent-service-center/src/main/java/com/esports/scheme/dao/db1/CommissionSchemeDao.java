package com.esports.scheme.dao.db1;

import com.esports.scheme.bean.db1.CommissionScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionSchemeDao extends JpaRepository<CommissionScheme, Long> {

    CommissionScheme findBySchemeCode(String schemeCode);

}

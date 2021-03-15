package com.esports.center.vip.dao.db1;

import com.esports.center.vip.bean.db1.VipRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VipRuleDao extends JpaRepository<VipRule, Long> {

    VipRule findByGrade(Integer vip);

}

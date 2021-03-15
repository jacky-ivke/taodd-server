package com.esports.scheme.dao.db1;

import com.esports.scheme.bean.db1.CommissionCfg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface CommissionCfgDao extends JpaRepository<CommissionCfg, Long> {

    @Query(nativeQuery = true, value = "select * from tb_commission_cfg c "
            + "inner join ("
            + "     select c.scheme_code,c.platform, c.game_type, min(c.id) id, max(c.profit_amount)profit_amount, max(c.member_num) member_num"
            + "     from tb_commission_cfg c where c.scheme_code=?1 and c.profit_amount<=?2 and c.member_num <= ?3 "
            + "     group by c.scheme_code,c.platform, c.game_type"
            + ")c1 "
            + " on c.scheme_code = c1.scheme_code and c.platform = c1.platform and c.game_type=c1.game_type "
            + " and c.profit_amount = c1.profit_amount and c.member_num = c1.member_num"
            + "")
    List<CommissionCfg> getCommissionSchemeCfg(String commissionCode, BigDecimal totalProfitAmount, Integer totalVaildMember);


    @Query(nativeQuery = true, value = "select c.platform platform, c.game_type gameType, 0 as 'percentage' from tb_commission_cfg c "
            + " where c.scheme_code=?1 "
            + " group by c.platform, c.game_type"
            + "")
    List<Map<String, Object>> getDefaultCommissionSchemeCfg(String commissionCode);
}

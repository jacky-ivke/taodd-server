 package com.esports.scheme.service;

 import com.esports.scheme.bean.db1.CommissionCfg;
 import com.esports.scheme.dao.db1.CommissionCfgDao;
 import com.esports.utils.JsonUtil;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;
 import org.springframework.util.CollectionUtils;

 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;

  @Service
 public class CommissionCfgManager {

      @Autowired
      private CommissionCfgDao commissionCfgDao;

      public List<CommissionCfg> getCommissionSchemeCfg(String commissionCode, BigDecimal totalProfitAmount, Integer totalVaildMember){
          List<CommissionCfg> list = commissionCfgDao.getCommissionSchemeCfg(commissionCode, totalProfitAmount, totalVaildMember);
          if(CollectionUtils.isEmpty(list)) {
              //当无法获取返佣方案时，防止页面出现空白
              list = this.getDefaultCommissionSchemeCfg(commissionCode);
          }
          return list;
      }


      public List<CommissionCfg> getDefaultCommissionSchemeCfg(String commissionCode){
          List<CommissionCfg> list = new ArrayList<CommissionCfg>();
          List<Map<String,Object>> cfgs = commissionCfgDao.getDefaultCommissionSchemeCfg(commissionCode);
          if(!CollectionUtils.isEmpty(cfgs)) {
              Iterator<Map<String,Object>> iterator = cfgs.iterator();
              CommissionCfg commissionCfg = null;
              while(iterator.hasNext()) {
                  Map<String,Object> map = iterator.next();
                  commissionCfg = JsonUtil.map2Object(map, CommissionCfg.class);
                  list.add(commissionCfg);
              }
          }
          return list;
      }
  }

package com.esports.rakeback.service;

import com.esports.api.center.MemberService;
import com.esports.rakeback.bean.db1.RakebackCfg;
import com.esports.rakeback.dao.db1.RakebackCfgDao;
import com.esports.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class RakebackCfgManager {

	@Autowired
	private RakebackCfgDao rakebackCfgDao;
	
	@Autowired
	private MemberService memberService;
	
	public List<RakebackCfg> getRakebackCfg(String rakeSchemeCode, Integer vip){
		List<RakebackCfg> list = rakebackCfgDao.findBySchemeCodeAndVip(rakeSchemeCode, vip);
		 if(CollectionUtils.isEmpty(list)) {
             //当无法获取返水方案时，防止页面出现空白
             list = this.getDefaultRakebackSchemeCfg(rakeSchemeCode);
         }
		return list;
	}
	
	public RakebackCfg getRakebackCfg(String account ,String platform, String gameType) {
        String schemeCode = memberService.getRakeSchemeCode(account);
        Integer vip = memberService.getVip(account);
	    RakebackCfg cfg = rakebackCfgDao.getRakebackCfg(schemeCode, vip, platform, gameType);
	    return cfg;
	}
	
    public List<RakebackCfg> getDefaultRakebackSchemeCfg(String commissionCode){
        List<RakebackCfg> list = new ArrayList<RakebackCfg>();
        List<Map<String,Object>> cfgs = rakebackCfgDao.getDefaultRakebackSchemeCfg(commissionCode);
        if(CollectionUtils.isEmpty(cfgs)) {
           return list;
        }
        Iterator<Map<String,Object>> iterator = cfgs.iterator();
        RakebackCfg rakebackCfg = null;
        while(iterator.hasNext()) {
            Map<String,Object> map = iterator.next();
            if(CollectionUtils.isEmpty(map)){
                continue;
            }
            rakebackCfg = JsonUtil.map2Object(map, RakebackCfg.class);
            list.add(rakebackCfg);
        }
        return list;
    }
    
	
}

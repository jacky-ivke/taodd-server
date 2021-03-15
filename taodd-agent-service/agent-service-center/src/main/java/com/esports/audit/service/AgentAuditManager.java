package com.esports.audit.service;

import com.esports.audit.bean.db1.AgentAudit;
import com.esports.audit.dao.db1.AgentAuditDao;
import com.esports.constant.WorkFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class AgentAuditManager {

    @Autowired
    private AgentAuditDao agentAuditDao;

    public boolean checkAuditStatus(String account) {
        boolean success = true;
        //待审核数据
        Integer okStatus = WorkFlow.Agent._PENDIGN_AUDIT.getCode();
        List<AgentAudit> list = agentAuditDao.findByAccountAndOkStatus(account, okStatus);
        if (!CollectionUtils.isEmpty(list)) {
            success = false;
        }
        return success;
    }

    public boolean checkAuditStatusByMobile(String mobile) {
        boolean success = true;
        //待审核数据
        Integer okStatus = WorkFlow.Agent._PENDIGN_AUDIT.getCode();
        List<AgentAudit> list = agentAuditDao.findByMobileAndOkStatus(mobile, okStatus);
        if (!CollectionUtils.isEmpty(list)) {
            success = false;
        }
        return success;
    }

    public void saveAgentAudit(AgentAudit audit) {
        agentAuditDao.save(audit);
    }
}

package com.esports.basic.service;

import com.esports.api.log.AgentLogService;
import com.esports.api.message.AgentMessageService;
import com.esports.api.order.AgentOrderService;
import com.esports.audit.service.AgentAuditManager;
import com.esports.bankcard.service.AgentBankCardManager;
import com.esports.basic.bean.db1.Agent;
import com.esports.basic.dao.db1.AgentCenterDao;
import com.esports.basic.dto.*;
import com.esports.cache.RedisUtil;
import com.esports.channel.bean.db1.DepositChannel;
import com.esports.channel.service.DepositChannelManager;
import com.esports.com.esports.security.code.service.SecurityCodeManager;
import com.esports.constant.*;
import com.esports.core.lock.DistributedReadWriteLock;
import com.esports.processor.ApiGateway;
import com.esports.scheme.service.CommissionSchemeManager;
import com.esports.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class AgentCenterManager {

    @Autowired
    private AgentCenterDao agentCenterDao;

    @Autowired
    private SecurityCodeManager securityCodeManager;

    @Autowired
    private AgentOrderService agentOrderService;

    @Autowired
    private AgentLogService agentLogService;

    @Autowired
    private AgentMessageService agentMessageService;

    @Autowired
    private AgentAuditManager agentAuditManager;

    @Autowired
    private AgentBankCardManager agentBankCardManager;

    @Autowired
    private CommissionSchemeManager commissionSchemeManager;

    @Autowired
    private DepositChannelManager depositChannelManager;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DistributedReadWriteLock redisLock;

    @Autowired
    private JWT jwt;

    private static final String DRAW_TO_BANK = "【取款至银行卡】取款人：%s";

    private static final String DRAW_TO_CENTER = "【取款至中心钱包】取款人：%s 附言：%s";

    private static final String DEPOSIT = "【代理存款】汇款人：%s 实名认证:%s";

    public Agent findByUserName(String username) {
        Agent player = agentCenterDao.findByAccount(username);
        if (null != player) {
            return player;
        }
        player = agentCenterDao.findByMobile(username);
        return player;
    }

    public boolean checkAccount(String account) {
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return false;
        }
        return true;
    }

    public boolean checkIdentity(String username) {
        boolean success = true;
        Agent player = agentCenterDao.findByAccount(username);
        if (null == player) {
            return success;
        }
        String identity = player.getIdentity();
        if (!PlayerCode._AGENT.getCode().equals(identity)) {
            success = false;
        }
        return success;
    }

    public boolean checkLoginErrorCount(String username, String ip) {
        boolean success = true;
        Agent player = this.findByUserName(username);
        if (null == player) {
            return success;
        }
        String account = player.getAccount();
        String redisKey = String.format(RedisCacheKey.AGENT_LOGIN_ERROR, account);
        Integer hasLoginErrorCount = redisUtil.hasKey(redisKey) ? Integer.valueOf(redisUtil.get(redisKey).toString()) : 0;
        if (hasLoginErrorCount <= GlobalCode._LOGIN_ERROR_LIMIT.getCode()) {
            return success;
        }
        success = false;
        String original = "正常";
        String target = "登录密码错误次数超过安全值";
        agentLogService.saveEventLog(account, LogTypeEnum._U_LOGIN_LIMIT.getType(), original, target, IPUtils.ipToLong(ip));
        return success;
    }

    public void setLoginErrorCount(String account, String ip) {
        String redisKey = String.format(RedisCacheKey.AGENT_LOGIN_ERROR, account);
        Integer hasLoginErrorCount = redisUtil.hasKey(redisKey) ? Integer.valueOf(redisUtil.get(redisKey).toString()) : 0;
        long expireTime = DateUtils.getDaySurplusTime();
        redisUtil.set(redisKey, ++hasLoginErrorCount, expireTime);
        String original = "";
        String target = String.valueOf(hasLoginErrorCount);
        agentLogService.saveEventLog(account, LogTypeEnum._C_LOGIN_ERROR.getType(), original, target, IPUtils.ipToLong(ip));
    }

    public void cleanLoginErrorCount(String account, String ip) {
        String redisKey = String.format(RedisCacheKey.AGENT_LOGIN_ERROR, account);
        boolean hasKey = redisUtil.hasKey(redisKey);
        if (hasKey) {
            redisUtil.del(redisKey);
        }
    }

    public boolean checkAllowDraw(String account) {
        boolean success = true;
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return success;
        }
        success = null != player.getIsDraw() ? player.getIsDraw().booleanValue() : true;
        return success;
    }

    public boolean checkCommissionScheme(String account, BigDecimal amount) {
        Agent player = agentCenterDao.findByAccount(account);
        String commissionCode = null != player ? player.getCommissionCode() : "";
        boolean success = commissionSchemeManager.checkCommissionRule(commissionCode, amount);
        return success;
    }

    public boolean checkCommissionBalance(String account, BigDecimal amount) {
        boolean success = false;
        Agent player = agentCenterDao.findByAccount(account);
        BigDecimal commissionBalance = null != player && null != player.getCommissionBalance() ? player.getCommissionBalance() : BigDecimal.ZERO;
        if (commissionBalance.compareTo(amount) >= 0) {
            success = true;
        }
        return success;
    }

    public boolean checkOtherBalance(String account, BigDecimal amount) {
        boolean success = false;
        Agent player = agentCenterDao.findByAccount(account);
        BigDecimal otherBalance = null != player && null != player.getOtherBalance() ? player.getOtherBalance() : BigDecimal.ZERO;
        if (otherBalance.compareTo(amount) >= 0) {
            success = true;
        }
        return success;
    }

    public boolean checkBankCard(String username) {
        boolean success = agentBankCardManager.checkBindCards(username);
        return success;
    }

    public boolean checkLoginPwd(String oldPassword, String password) {
        boolean success = false;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, oldPassword)) {
            success = true;
        }
        return success;
    }

    public boolean checkAgentAuditStatus(String account) {
        boolean success = agentAuditManager.checkAuditStatus(account);
        return success;
    }

    public boolean checkAgentAuditStatusByMobile(String mobile) {
        boolean success = agentAuditManager.checkAuditStatusByMobile(mobile);
        return success;
    }

    public boolean checkAuthCode(String key, String code) {
        boolean success = securityCodeManager.checkCode(key, code);
        return success;
    }

    public boolean checkSmsCode(String mobile, String code) {
        boolean success = securityCodeManager.checkSmsCode(mobile, code);
        return success;
    }

    public boolean checkEmailCode(String email, String code) {
        boolean success = securityCodeManager.checkEmailCode(email, code);
        return success;
    }

    public boolean checkBindMobile(String mobile) {
        Agent Player = agentCenterDao.findByMobile(mobile);
        if (null == Player) {
            return true;
        }
        return false;
    }

    public boolean checkEmail(String account, String email) {
        boolean success = false;
        Agent player = agentCenterDao.findByEmail(email);
        if (null == player) {
            success = true;
        }
        //检查账号绑定的邮箱是否与当前邮箱一致
        if (account.equalsIgnoreCase(player.getAccount())) {
            success = true;
        }
        return success;
    }

    public boolean checkTradePwd(String account, String password) {
        boolean success = false;
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return false;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, player.getTradePwd())) {
            success = true;
        }
        return success;
    }

    public boolean checkSubMember(String leader, String member) {
        boolean success = false;
        Agent player = agentCenterDao.findByAccount(member);
        if (null == player) {
            return false;
        }
        if (leader.equalsIgnoreCase(player.getLeader())) {
            success = true;
        }
        return success;
    }

    public String login(String account, String client, Integer source, Long ip) {
        String token = jwt.generateToken(account);
        Long expireTime = jwt.getExpire();
        agentLogService.saveLoginLog(account, client, source, ip);
        this.cleanLoginErrorCount(account, IPUtils.longToIp(ip));
        this.sso(account, token, expireTime);
        return token;
    }

    protected void sso(String account, String token, Long expire) {
        try {
            String redisKey = String.format(RedisCacheKey.AGENT_SSO_KEY, account);
            redisUtil.set(redisKey, token, expire);
        } catch (Exception e) {
            log.error("【代理单点登录】代理账号：{}, 异常信息：{}", account, e.getMessage());
        }
    }

    public LoginDto region(String account, String ip) {
        LoginDto dto = new LoginDto();
        dto.setCity(IPUtils.getCity(ip));
        dto.setIp(ip);
        dto.setAccount(account);
        return dto;
    }

    public void updateMobile(String account, String mobile, String ip) {
        Agent player = this.findByUserName(account);
        if (null == player) {
            return;
        }
        String originl = player.getMobile();
        String loginType = LogTypeEnum._U_MOBILE.getType();
        player.setMobile(mobile);
        agentCenterDao.save(player);
        agentLogService.saveEventLog(account, loginType, originl, mobile, IPUtils.ipToLong(ip));
    }

    public void updateRealName(String account, String realName, String ip) {
        Agent player = this.findByUserName(account);
        if (null == player) {
            return;
        }
        String originl = player.getRealName();
        String logType = LogTypeEnum._U_REALNAME.getType();
        player.setRealName(realName);
        agentCenterDao.save(player);
        agentLogService.saveEventLog(account, logType, originl, realName, IPUtils.ipToLong(ip));
    }

    public void updateEmail(String account, String email, String ip) {
        Agent player = this.findByUserName(account);
        if (null == player) {
            return;
        }
        String originl = player.getEmail();
        String logType = LogTypeEnum._U_EMAIL.getType();
        player.setEmail(email);
        agentCenterDao.save(player);
        agentLogService.saveEventLog(account, logType, originl, email, IPUtils.ipToLong(ip));
    }

    public void updateTradePwd(String account, String password, String ip) {
        Agent player = this.findByUserName(account);
        if (null == player) {
            return;
        }
        String originl = "";
        String logType = LogTypeEnum._U_TRADE_PWD.getType();
        String encodePwd = new BCryptPasswordEncoder().encode(password);
        player.setTradePwd(encodePwd);
        agentCenterDao.save(player);
        agentLogService.saveEventLog(account, logType, originl, password, IPUtils.ipToLong(ip));
    }

    public BigDecimal updateCommissionBalance(String account, BigDecimal amount) {
        BigDecimal commissionBalance = BigDecimal.ZERO;
        try {
            redisLock.lock(account, account);
            Agent player = agentCenterDao.findByAccount(account);
            if (null == player) {
                return commissionBalance;
            }
            commissionBalance = null != player.getCommissionBalance() ? player.getCommissionBalance().add(amount) : commissionBalance;
            player.setCommissionBalance(commissionBalance);
            agentCenterDao.save(player);
        } finally {
            redisLock.unlock(account, account);
        }
        return commissionBalance;
    }

    public BigDecimal updateCommissionBalanceAndCenter(String account, BigDecimal amount, BigDecimal balance) {
        BigDecimal commissionBalance = BigDecimal.ZERO;
        BigDecimal centerBalance = BigDecimal.ZERO;
        try {
            redisLock.lock(account, account);
            Agent player = agentCenterDao.findByAccount(account);
            if (null == player) {
                return commissionBalance;
            }
            commissionBalance = null != player.getCommissionBalance() ? player.getCommissionBalance().add(amount) : commissionBalance;
            centerBalance = null != player.getBalance() ? player.getBalance().add(balance) : centerBalance;
            player.setCommissionBalance(commissionBalance);
            player.setBalance(centerBalance);
            agentCenterDao.save(player);
        } finally {
            redisLock.unlock(account, account);
        }
        return commissionBalance;
    }

    public BigDecimal updateOtherBalance(String account, BigDecimal amount) {
        BigDecimal otherBalance = BigDecimal.ZERO;
        try {
            redisLock.lock(account, account);
            Agent player = agentCenterDao.findByAccount(account);
            if (null == player) {
                return otherBalance;
            }
            otherBalance = null != player.getOtherBalance() ? player.getOtherBalance().add(amount) : otherBalance;
            player.setOtherBalance(otherBalance);
            agentCenterDao.save(player);
        } finally {
            redisLock.unlock(account, account);
        }
        return otherBalance;
    }

    public BigDecimal updateCenterBalance(String account, BigDecimal amount) {
        BigDecimal centerBalance = BigDecimal.ZERO;
        try {
            redisLock.lock(account, account);
            Agent player = agentCenterDao.findByAccount(account);
            if (null == player) {
                return centerBalance;
            }
            centerBalance = null != player.getBalance() ? player.getBalance().add(amount) : centerBalance;
            player.setBalance(centerBalance);
            agentCenterDao.save(player);
        } finally {
            redisLock.unlock(account, account);
        }
        return centerBalance;
    }

    public String getRealName(String account) {
        String realName = "";
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return realName;
        }
        realName = player.getRealName();
        return realName;
    }

    public BigDecimal getOtherBalance(String account) {
        BigDecimal balance = BigDecimal.ZERO;
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return balance;
        }
        balance = null != player.getOtherBalance() ? player.getOtherBalance() : balance;
        return balance;
    }

    public Integer getTotalSubMember(String account) {
        Integer subMember = agentCenterDao.getTotalSubMember(account);
        return (null == subMember ? 0 : subMember);
    }

    public Integer getCurrMonthRegSubMember(String account) {
        Integer subMember = agentCenterDao.getCurrMonthRegSubMember(account);
        return (null == subMember ? 0 : subMember);
    }

    public List<Map<String, Object>> getBilltype(String walletType) {
        List<Map<String, Object>> list = null;
        if (WalletCode._COMMISSION.getCode().equals(walletType)) {
            list = agentCenterDao.getCommissionBillType();
            return list;
        }
        list = agentCenterDao.getOtherBillType();
        return list;
    }

    public AgentExtProfileDto getExtData(String account, Integer source, String ip) {
        AgentExtProfileDto extProfileDto = new AgentExtProfileDto();
        String identity = PlayerCode._MEMBER.getCode();
        String json = agentMessageService.getUnreadMsg(account, source, identity);
        AgentProfileDto profileDto = this.getAgentProfile(account, ip);
        UnReadMsgDto msgDto = StringUtils.isEmpty(json) ? null : JsonUtil.string2Object(json, UnReadMsgDto.class);
        extProfileDto.setUserBaseInfo(profileDto);
        extProfileDto.setUnreadMsgAmounts(msgDto);
        return extProfileDto;
    }

    public AgentProfileDto getAgentProfile(String account, String ip) {
        AgentProfileDto dto = null;
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return dto;
        }
        dto = new AgentProfileDto();
        BeanUtils.copyProperties(player, dto);
        Integer subMember = this.getTotalSubMember(account);
        Boolean isCompleteTradePwd = StringUtils.isEmpty(player.getTradePwd()) ? Boolean.FALSE : Boolean.TRUE;
        dto.setIsCompleteTradePwd(isCompleteTradePwd);
        dto.setSubMember(subMember);
        dto.setIp(ip);
        dto.setCity(IPUtils.getCity(ip));
        return dto;
    }

    public ExtenstionLinksDto getExtensionLinks(String account) {
        ExtenstionLinksDto dto = new ExtenstionLinksDto();
        Agent player = agentCenterDao.findByAccount(account);
        if (null == player) {
            return dto;
        }
        dto.setPcLink(this.getLinks(player.getPcexclusiveLinks(), player.getInviteCode()));
        return dto;
    }

    private String getLinks(String url, String inviteCode) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String links = url + "?iCode=" + inviteCode;
        if (url.indexOf("?") >= 0) {
            String params = "&iCode=" + inviteCode;
            links = url + params;
        }
        return links;
    }

    public FinancialCenterDto getFinancialCenter(String account) {
        FinancialCenterDto dto = new FinancialCenterDto();
        Agent player = agentCenterDao.findByAccount(account);
        BigDecimal commissionBalance = null != player && null != player.getCommissionBalance() ? player.getCommissionBalance() : BigDecimal.ZERO;
        BigDecimal otherBalance = null != player && null != player.getOtherBalance() ? player.getOtherBalance() : BigDecimal.ZERO;
        BigDecimal drawPendingAmount = agentOrderService.getAgentPendingDrawAmount(account);
        dto.setCommissionBalance(commissionBalance);
        dto.setOtherBalance(otherBalance);
        dto.setDrawPendingAmount(drawPendingAmount);
        return dto;
    }

    public PageData getSubMember(String leader, String account, Integer page, Integer pageSize, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("regTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<Agent> spec = new Specification<Agent>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Agent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("type").as(Integer.class), GlobalCode._IDENTITY_FORMAL.getCode()));
                if (!StringUtils.isEmpty(leader)) {
                    predicate.getExpressions().add(cb.equal(root.get("leader").as(String.class), leader));
                }
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.like(root.get("account").as(String.class), "%" + account + "%"));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("regTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("regTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        Page<Agent> pages = agentCenterDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<SubMemberDto> dtos = this.assembleData((List<Agent>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    private List<SubMemberDto> assembleData(List<Agent> players) {
        List<SubMemberDto> dtos = new ArrayList<SubMemberDto>();
        if (CollectionUtils.isEmpty(players)) {
            return dtos;
        }
        SubMemberDto dto = null;
        Iterator<Agent> itrator = players.iterator();
        while (itrator.hasNext()) {
            Agent player = itrator.next();
            dto = new SubMemberDto();
            BeanUtils.copyProperties(player, dto);
            String account = player.getAccount();
            dto = this.getMemberSummary(dto, account);
            dtos.add(dto);
        }
        return dtos;
    }

    protected SubMemberDto getMemberSummary(SubMemberDto dto, String account) {
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        FutureTask<Integer> t1 = this.getMemberDepositAmount(dto, account);
        FutureTask<Integer> t2 = this.getMemberDrawAmount(dto, account);
        FutureTask<Integer> t3 = this.getMemberProfitAmount(dto, account);
        FutureTask<Integer> t4 = this.getMemberLastLoginTime(dto, account);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        tasks.add(t4);
        ApiGateway.syncwait(tasks);
        return dto;
    }

    public FutureTask<Integer> getMemberDepositAmount(final SubMemberDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal depositTotalAmount = agentOrderService.getMemberDepositAmount(account);
                synchronized (dto) {
                    dto.setDepositAmount(depositTotalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getMemberDrawAmount(final SubMemberDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal drawTotalAmount = agentOrderService.getMemberDrawAmount(account);
                synchronized (dto) {
                    dto.setDrawAmount(drawTotalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getMemberProfitAmount(final SubMemberDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                BigDecimal betWinTotalAmount = agentOrderService.getMemberProfitAmount(account);
                synchronized (dto) {
                    dto.setProfitAmount(betWinTotalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public FutureTask<Integer> getMemberLastLoginTime(final SubMemberDto dto, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                String lastLoginTime = agentLogService.getLastLoginTime(account);
                if (StringUtils.isEmpty(lastLoginTime)) {
                    return 0;
                }
                synchronized (dto) {
                    dto.setLastLoginTime(lastLoginTime);
                }
                return 1;
            }
        });
        return futureTask;
    }


    public String withdrawalToBank(Integer source, String account, BigDecimal amount, String bankRealName, String bankName, String bankAccount, String ip) {
        String walletType = WalletCode._COMMISSION.getCode();
        BigDecimal costAmount = BigDecimal.ZERO;
        Integer okStatus = OrderCode._PENDING.getCode();
        String remarks = String.format(DRAW_TO_BANK, account);
        Integer auditStatus = WorkFlow.Draw._PENDIGN_AUDIT.getCode();
        BigDecimal balance = this.updateCommissionBalance(account, amount.negate());
        String orderNo = agentOrderService.saveDrawOrder(walletType, okStatus, auditStatus, costAmount, source, account, amount, balance, bankRealName, bankName, bankAccount, ip, remarks, true);
        return orderNo;
    }

    public String withdrawalToBalance(Integer source, String account, BigDecimal amount, String remarks, String ip) {
        String walletType = WalletCode._COMMISSION.getCode();
        BigDecimal costAmount = BigDecimal.ZERO;
        String bankRealName = this.getRealName(account);
        String bankName = WalletCode._BALANCE.getMessage();
        String bankAccount = WalletCode._BALANCE.getCode();
        Integer okStatus = OrderCode._SUCCESS.getCode();
        remarks = String.format(DRAW_TO_CENTER, account, remarks);
        Integer auditStatus = WorkFlow.Draw._AUDIT_SUCCESS.getCode();
        BigDecimal balance = this.updateCommissionBalanceAndCenter(account, amount.negate(), amount);
        String orderNo = agentOrderService.saveDrawOrder(walletType, okStatus, auditStatus, costAmount, source, account, amount, balance, bankRealName, bankName, bankAccount, ip, remarks, false);
        //取款至中心账户，无需审核,发送站内信息
        return orderNo;
    }

    public JSONObject createAgentDepositOrder(Integer source, String account, String remitter, BigDecimal amount, Long channelId, String ip) {
        DepositChannel channel = depositChannelManager.getChannelRule(channelId);
        String bankRealName = channel.getBankRealName();
        String bankAccount = channel.getBankAccount();
        String payType = channel.getPayType();
        String channelType = channel.getChannelType();
        String channelName = channel.getChannelName();
        String remarks = String.format(DEPOSIT, account, remitter);
        Integer okStatus = OrderCode._PENDING.getCode();
        String walletType = WalletCode._OTHER.getCode();
        BigDecimal balance = this.getOtherBalance(account);
        JSONObject result = agentOrderService.saveDepositOrder(walletType, bankRealName, bankAccount, payType, channelType, channelName, okStatus, source, account, amount, balance, ip, remarks);
        return result;
    }
}

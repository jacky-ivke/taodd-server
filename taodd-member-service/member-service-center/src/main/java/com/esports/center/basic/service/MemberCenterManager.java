package com.esports.center.basic.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.log.MemberLogService;
import com.esports.api.message.MemberMessgeService;
import com.esports.api.order.MemberOrderService;
import com.esports.cache.RedisUtil;
import com.esports.center.audit.bean.db1.AgentAudit;
import com.esports.center.audit.service.AgentAuditManager;
import com.esports.center.bankcard.service.MemberBankCardManager;
import com.esports.center.basic.bean.db1.Member;
import com.esports.center.basic.dao.db1.MemberCenterDao;
import com.esports.center.basic.dto.*;
import com.esports.center.channel.bean.db1.DepositChannel;
import com.esports.center.channel.service.DepositChannelManager;
import com.esports.center.domain.bean.db1.Domain;
import com.esports.center.domain.service.DomainManager;
import com.esports.center.scheme.bean.db1.DepositScheme;
import com.esports.center.scheme.service.DepositSchemeManager;
import com.esports.center.scheme.service.DrawSchemeManager;
import com.esports.center.scheme.service.GradeSchemeManager;
import com.esports.center.vip.service.VipRuleManager;
import com.esports.com.esports.security.code.service.SecurityCodeManager;
import com.esports.constant.*;
import com.esports.core.lock.DistributedReadWriteLock;
import com.esports.processor.ApiGateway;
import com.esports.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
@Slf4j
public class MemberCenterManager {

    private static final String DEPOSIT = "【会员存款】汇款人：%s, 实名认证:%s";

    private static final String DRAW = "【会员取款】取款人：%s";

    private static final String JOINT_PLAN = "【会员申请加入合营计划】";

    @Autowired
    private MemberCenterDao memberCenterDao;

    @Autowired
    private MemberLogService memberLogService;

    @Autowired
    private SecurityCodeManager securityCodeManager;

    @Autowired
    private MemberOrderService memberOrderService;

    @Autowired
    private MemberMessgeService memberMessgeService;

    @Autowired
    private DomainManager domainManager;

    @Autowired
    private GradeSchemeManager gradeSchemeManager;

    @Autowired
    private VipRuleManager vipRuleManager;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DistributedReadWriteLock redisLock;

    @Autowired
    private JWT jwt;

    @Autowired
    private DrawSchemeManager drawSchemeManager;

    @Autowired
    private DepositChannelManager depositChannelManager;

    @Autowired
    private MemberBankCardManager memberBankCardManager;

    @Autowired
    private AgentAuditManager agentAuditManager;

    @Autowired
    private DepositSchemeManager depositSchemeManager;

    /**
     * 忘记密码修改密码界面，发验证码的手机和提交手机号校验失效时间120s
     */
    private static final long SENCOND_EXPIRE_TIME = 120;


    public Member findByUserName(String username) {
        Member Player = memberCenterDao.findByAccount(username);
        if (null == Player) {
            Player = memberCenterDao.findByMobile(username);
        }
        return Player;
    }

    public boolean checkLoginErrorCount(String username, String ip) {
        boolean success = true;
        Member player = this.findByUserName(username);
        if (null == player) {
            return success;
        }
        String account = player.getAccount();
        String redisKey = String.format(RedisCacheKey.MEMBER_LOGIN_ERROR, account);
        Integer hasLoginErrorCount = redisUtil.hasKey(redisKey) ? Integer.valueOf(redisUtil.get(redisKey).toString()) : 0;
        if (hasLoginErrorCount <= GlobalCode._LOGIN_ERROR_LIMIT.getCode()) {
            return success;
        }
        success = false;
        String original = "正常";
        String target = "登录密码错误次数超过安全值";
        memberLogService.saveEventLog(account, LogTypeEnum._U_LOGIN_LIMIT.getType(), original, target, IPUtils.ipToLong(ip));
        return success;
    }

    public void setLoginErrorCount(String account, String ip) {
        String redisKey = String.format(RedisCacheKey.MEMBER_LOGIN_ERROR, account);
        Integer hasLoginErrorCount = redisUtil.hasKey(redisKey) ? Integer.valueOf(redisUtil.get(redisKey).toString()) : 0;
        long expireTime = DateUtils.getDaySurplusTime();
        redisUtil.set(redisKey, ++hasLoginErrorCount, expireTime);
        String original = "";
        String target = String.valueOf(hasLoginErrorCount);
        memberLogService.saveEventLog(account, LogTypeEnum._C_LOGIN_ERROR.getType(), original, target, IPUtils.ipToLong(ip));
    }

    public void cleanLoginErrorCount(String account, String ip) {
        String redisKey = String.format(RedisCacheKey.MEMBER_LOGIN_ERROR, account);
        boolean hasKey = redisUtil.hasKey(redisKey);
        if (hasKey) {
            redisUtil.del(redisKey);
        }
    }

    public boolean checkDepositRule(Long channelId, BigDecimal amount) {
        boolean success = depositChannelManager.checkDepositRule(channelId, amount);
        return success;
    }


    public boolean checkDepositTotalNum(String account) {
        boolean success = true;
        DepositScheme scheme = depositSchemeManager.getDepositScheme();
        Integer limitTotal = null != scheme ? scheme.getDepositTotalNum() : 0;
        JSONObject jsonObject = memberOrderService.getTodayOnlineDepositTotal(account);
        if (null == jsonObject) {
            return success;
        }
        DepositTotalDto dto = JsonUtil.string2Object(jsonObject.toString(), DepositTotalDto.class);
        Integer depositTotalCount = null != dto && null != dto.getDepositTotalCount() ? dto.getDepositTotalCount() : 0;
        limitTotal = null == limitTotal || limitTotal <= 0 ? 0 : limitTotal;
        depositTotalCount = (null == depositTotalCount || depositTotalCount <= 0) ? 0 : depositTotalCount;
        if (depositTotalCount > limitTotal) {
            success = false;
        }
        return success;
    }

    public boolean checkDepositFailureRate(String account) {
        boolean success = true;
        DepositScheme scheme = depositSchemeManager.getDepositScheme();
        JSONObject jsonObject = memberOrderService.getTodayOnlineDepositTotal(account);
        if (null == jsonObject) {
            return success;
        }
        Integer limitNum = null != scheme ? scheme.getDepositNum() : 0;
        BigDecimal limitRate = null != scheme ? scheme.getDepositFailureRate() : BigDecimal.ZERO;
        DepositTotalDto dto = JsonUtil.string2Object(jsonObject.toString(), DepositTotalDto.class);
        Integer depositNum = null != dto && null != dto.getDepositNum() ? dto.getDepositNum() : 0;
        Integer depositSuccessNum = null != dto && null != dto.getDepositSuccessNum() ? dto.getDepositSuccessNum() : 0;
        Integer depositFailureNum = null != dto && null != dto.getDepositFailureNum() ? dto.getDepositFailureNum() : 0;
        limitNum = null == limitNum || limitNum <= 0 ? 0 : limitNum;
        depositNum = (null == depositNum || depositNum <= 0) ? 0 : depositNum;
        limitRate = null == limitRate ? BigDecimal.ZERO : limitRate;
        //10分钟之类订单数过多，则开启失败率预警机制
        if (depositNum > limitNum) {
            depositNum = depositNum <= 0 ? 1 : depositNum;
            BigDecimal failureRate = new BigDecimal(depositFailureNum).divide(new BigDecimal(depositNum)).multiply(new BigDecimal("100"));
            if (failureRate.compareTo(limitRate) > 0) {
                success = false;
            }
        }
        return success;
    }

    public boolean checkAllowDraw(String account, String drawSchemeCode, Integer vip) {
        boolean success = drawSchemeManager.checkAllowDraw(drawSchemeCode, vip);
        return success;
    }

    public boolean checkDrawScope(BigDecimal amount, String drawSchemeCode, Integer vip) {
        boolean success = drawSchemeManager.checkDrawScope(drawSchemeCode, vip, amount);
        return success;
    }

    public boolean checkDrawTotalOrDrawCount(String account, String drawSchemeCode, Integer vip) {
        boolean success = true;
        JSONObject json = memberOrderService.getTodayDrawTotal(account);
        DrawTotalDto dto = JsonUtil.string2Object(json.toString(), DrawTotalDto.class);
        Integer drawCount = null != dto && null != dto.getDrawCount() ? dto.getDrawCount() : 0;
        BigDecimal drawTotal = null != dto && null != dto.getDrawAmount() ? dto.getDrawAmount() : BigDecimal.ZERO;
        success = drawSchemeManager.checkDrawTotalOrDrawCount(drawSchemeCode, vip, drawCount, drawTotal);
        return success;
    }

    public boolean checkLoginPwd(Member player, String password) {
        boolean success = false;
        String dbPassword = player.getLoginPwd();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, dbPassword)) {
            success = true;
        }
        return success;
    }

    public boolean checkAccount(String account) {
        Member player = this.findByUserName(account);
        if (null == player) {
            return true;
        }
        return false;
    }

    public boolean checkInviteCode(String inviteCode) {
        if (StringUtils.isEmpty(inviteCode)) {
            return true;
        }
        Member player = memberCenterDao.findByInviteCode(inviteCode);
        if (null == player) {
            return false;
        }
        return true;
    }

    public boolean checkBindMobile(String mobile) {
        Member player = memberCenterDao.findByMobile(mobile);
        if (null == player) {
            return false;
        }
        return true;
    }

    public boolean checkHasMobile(String account) {
        boolean success = false;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return success;
        }
        String bindMobile = player.getMobile();
        if (!StringUtils.isEmpty(bindMobile)) {
            success = true;
        }
        return success;
    }

    public boolean checkBindMobile(String account, String mobile) {
        boolean success = false;
        Member player = memberCenterDao.findByMobile(mobile);
        if (null == player) {
            return true;
        }
        //检查账号绑定的手机是否与当前手机号一致
        if (account.equalsIgnoreCase(player.getAccount())) {
            success = true;
        }
        return success;
    }

    public boolean checkEmail(String account, String email) {
        boolean success = false;
        Member player = memberCenterDao.findByEmail(email);
        if (null == player) {
            return true;
        }
        //检查账号绑定的邮箱是否与当前邮箱一致
        if (account.equalsIgnoreCase(player.getAccount())) {
            success = true;
        }
        return success;
    }

    public boolean checkQQ(String account, String qq) {
        boolean success = true;
        if (StringUtils.isEmpty(qq)) {
            return true;
        }
        Member player = memberCenterDao.findByQq(qq);
        if (null == player) {
            return true;
        }
        if (!account.equals(player.getAccount())) {
            success = false;
        }
        return success;
    }

    public boolean checkWechat(String account, String wechat) {
        boolean success = true;
        if (StringUtils.isEmpty(wechat)) {
            return true;
        }
        Member player = memberCenterDao.findByWechat(wechat);
        if (null == player) {
            return true;
        }
        if (!account.equals(player.getAccount())) {
            success = false;
        }
        return success;
    }

    public boolean checkProfile(String account, String qq, String wechat, String realName) {
        boolean success = true;
        Member player = memberCenterDao.findByAccount(account);
        String authName = null != player ? player.getRealName() : "";
        if (!StringUtils.isEmpty(authName) && !realName.equalsIgnoreCase(authName)) {
            success = false;
            return success;
        }
        String authQq = null != player? player.getQq() : "";
        if (!StringUtils.isEmpty(authQq) && !qq.equalsIgnoreCase(authQq)) {
            success = false;
            return success;
        }
        String authWechat = null != player? player.getWechat() : "";
        if (!StringUtils.isEmpty(authWechat) && !wechat.equalsIgnoreCase(authWechat)) {
            success = false;
            return success;
        }
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

    public boolean checkBalance(String account, BigDecimal amount) {
        boolean success = true;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return true;
        }
        BigDecimal balance = player.getBalance();
        if (amount.compareTo(balance) > 0) {
            success = false;
        }
        return success;
    }

    public boolean checkBankCard(String username) {
        boolean success = memberBankCardManager.checkBindCards(username);
        return success;
    }

    public boolean checkIdentity(String username) {
        boolean success = true;
        Member player = memberCenterDao.findByAccount(username);
        if (null == player) {
            return success;
        }
        String identity = player.getIdentity();
        if (!PlayerCode._AGENT.getCode().equals(identity)) {
            success = false;
        }
        return success;
    }

    public boolean checkAgentAuditStatus(String account) {
        boolean success = agentAuditManager.checkAuditStatus(account);
        return success;
    }

    public boolean checkTradePwd(String account, String password) {
        boolean success = false;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return false;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, player.getTradePwd())) {
            success = true;
        }
        return success;
    }

    public boolean checkForgetMobile(String mobile) {
        boolean success = false;
        Member player = memberCenterDao.findByMobile(mobile);
        if (null == player) {
            return success;
        }
        String account = player.getAccount();
        String redisKey = String.format(RedisCacheKey.SECOND_CHEK_KEY, account);
        String value = redisUtil.hasKey(redisKey) ? redisUtil.get(redisKey).toString() : "";
        if (mobile.equalsIgnoreCase(value)) {
            success = true;
            redisUtil.del(redisKey);
        }
        return success;
    }

    public boolean checkForgetVerify(String mobile) {
        Member player = memberCenterDao.findByMobile(mobile);
        if (null == player) {
            return false;
        }
        String account = player.getAccount();
        String redisKey = String.format(RedisCacheKey.SECOND_CHEK_KEY, account);
        redisUtil.set(redisKey, mobile, SENCOND_EXPIRE_TIME);
        return true;
    }

    protected void sso(String account, String token, Long expire) {
        try {
            String redisKey = String.format(RedisCacheKey.MEMBER_SSO_KEY, account);
            redisUtil.set(redisKey, token, expire);
        } catch (Exception e) {
            log.error("【会员单点登录】会员账号：{}, 异常信息：{}", account, e.getMessage());
        }
    }

    public String getPlayerInviter(String inviteCode) {
        String inviter = null;
        if (StringUtils.isEmpty(inviteCode)) {
            return inviter;
        }
        Member player = memberCenterDao.findByInviteCode(inviteCode);
        if (null == player) {
            return inviter;
        }
        inviter = player.getAccount();
        return inviter;
    }

    public BigDecimal getMemberBalance(String account) {
        BigDecimal balance = BigDecimal.ZERO;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return balance;
        }
        balance = null != player.getBalance() ? player.getBalance() : balance;
        return balance;
    }

    public BigDecimal getInterestBalance(String account) {
        BigDecimal balance = BigDecimal.ZERO;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return balance;
        }
        balance = null != player.getInterestBalance() ? player.getInterestBalance() : balance;
        return balance;
    }

    public String getRakeScheme(String gradeCode) {
        String rakeScheme = memberCenterDao.getRakeScheme(gradeCode);
        return rakeScheme;
    }

    public String getDrawScheme(String account) {
        String drawScheme = memberCenterDao.getDrawScheme(account);
        return drawScheme;
    }

    public Integer getVip(String account) {
        Integer vip = 0;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return vip;
        }
        vip = null != player.getVip() ? player.getVip() : vip;
        return vip;
    }

    public String getGradeCode(String account) {
        String gradeCode = "";
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return gradeCode;
        }
        gradeCode = !StringUtils.isEmpty(player.getGradeCode()) ? player.getGradeCode() : gradeCode;
        return gradeCode;
    }

    public String getRakeSchemeCode(String account){
        String gradeCode = this.getGradeCode(account);
        String rakeSchemeCode = memberCenterDao.getRakeScheme(gradeCode);
        return rakeSchemeCode;
    }

    public String getRealName(String account) {
        String realName = "";
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return realName;
        }
        realName = player.getRealName();
        return realName;
    }

    public String login(String account, String client, Integer source, Long ip) {
        String token = jwt.generateToken(account);
        Long expireTime = jwt.getExpire();
        memberLogService.saveLoginLog(account, client, source, ip);
        this.cleanLoginErrorCount(account, IPUtils.longToIp(ip));
        this.sso(account, token, expireTime);
        return token;
    }

    private Member initAgentRelation(Member player, String icode) {
        String defTopLeader = GlobalCode._DEFAULT_TOP_AGENT.getMessage();
        String defLeader = GlobalCode._DEFAULT_AGENT.getMessage();
        String defGradeCode = gradeSchemeManager.getDefaultGradeCode();
        if (StringUtils.isEmpty(icode)) {
            player.setTopLeader(defTopLeader);
            player.setLeader(defLeader);
            player.setGradeCode(defGradeCode);
        } else {
            Member member = memberCenterDao.findByInviteCode(icode);
            String topLeader = null != member ? member.getTopLeader() : defTopLeader;
            String leader = null != member ? player.getAccount() : defLeader;
            String gradeCode = null != member && !StringUtils.isEmpty(member.getGradeCode()) ? member.getGradeCode() : defGradeCode;
            player.setTopLeader(topLeader);
            player.setLeader(leader);
            player.setGradeCode(gradeCode);
        }
        return player;
    }

    private Member initInvitationLink(Member player) {
        Domain domain = domainManager.getDefaultDomain(DomainCode._MAIN.getCode());
        String link = null != domain ? domain.getUrl() : "";
        player.setH5exclusiveLinks(link);
        player.setPcexclusiveLinks(link);
        return player;
    }

    public void saveJoinPlan(String account, Integer source, String ip) {
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return;
        }
        AgentAudit agentAudit = new AgentAudit();
        String remarks = JOINT_PLAN;
        Integer auditStatus = WorkFlow.Agent._PENDIGN_AUDIT.getCode();
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        String transactionId = RandomUtil.getUUID("");
        BeanUtils.copyProperties(player, agentAudit);
        agentAudit.setId(null);
        agentAudit.setTransactionId(transactionId);
        agentAudit.setCreateTime(createTime);
        agentAudit.setOkStatus(auditStatus);
        agentAudit.setSource(null == source ? player.getPlatform() : source);
        agentAudit.setRemarks(remarks);
        agentAudit.setIp(IPUtils.ipToLong(ip));
        agentAudit.setArea(IPUtils.getArea(ip));
        agentAudit.setTopLeader(player.getTopLeader());
        agentAudit.setLeader(player.getLeader());
        agentAuditManager.saveAgentAudit(agentAudit);
        String loginType = LogTypeEnum._C_AGENT_APPLY.getType();
        String originl = PlayerCode._MEMBER.getMessage();
        String tartget = PlayerCode._AGENT.getMessage();
        memberLogService.saveEventLog(account, loginType, originl, tartget, IPUtils.ipToLong(ip));
    }

    public void register(String account, String password, String mobile, String inviteCode, String iCode, Integer source, String ip) {
        this.save(account, password, mobile, inviteCode, null, iCode, source, ip);
    }

    public void register(String account, String password, String inviter, Integer source, String ip) {
        this.save(account, password, null, null, inviter, null, source, ip);
    }

    private void save(String account, String password, String mobile, String inviteCode, String inviter, String iCode, Integer source, String ip) {
        Member Player = new Member();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String identity = PlayerCode._MEMBER.getCode();
        inviter = StringUtils.isEmpty(inviteCode) ? inviter : this.getPlayerInviter(inviteCode);
        Player.setRegTime(now);
        Player.setApprovalTime(now);
        Player.setOkStatus(GlobalCode._ENABLE.getCode());
        Player.setIsDraw(Boolean.TRUE);
        Player.setBalance(BigDecimal.ZERO);
        Player.setInterestBalance(BigDecimal.ZERO);
        Player.setCommissionBalance(BigDecimal.ZERO);
        Player.setLocked(Boolean.FALSE);
        Player.setType(GlobalCode._IDENTITY_FORMAL.getCode());
        Player.setAccount(account);
        Player.setLoginPwd(new BCryptPasswordEncoder().encode(password));
        Player.setMobile(mobile);
        Player.setInviteCode(RandomUtil.getRandomString(5));
        Player.setInviter(inviter);
        Player.setIp(IPUtils.ipToLong(ip));
        Player.setArea(IPUtils.getArea(ip));
        Player.setIdentity(identity);
        Player = this.initAgentRelation(Player, iCode);
        Player = this.initInvitationLink(Player);
        memberCenterDao.save(Player);
    }

    public LoginDto region(String account, String ip) {
        LoginDto dto = new LoginDto();
        dto.setCity(IPUtils.getCity(ip));
        dto.setIp(ip);
        dto.setAccount(account);
        return dto;
    }

    public DrawScopeDto getDrawRules(String account) {
        String drawSchemeCode = memberCenterDao.getDrawScheme(account);
        Integer vip = this.getVip(account);
        DrawScopeDto dto = drawSchemeManager.getDrawScopLimit(drawSchemeCode, vip);
        return dto;
    }

    public List<Map<String, Object>> getTradeBillType() {
        List<Map<String, Object>> list = memberCenterDao.getTradeBillType();
        return list;
    }

    public List<Map<String, Object>> getGameTypes() {
        List<Map<String, Object>> list = memberCenterDao.getGameTypes();
        return list;
    }


    public BigDecimal getBalance(String account) {
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = null != player.getBalance() ? player.getBalance() : BigDecimal.ZERO;
        return balance;
    }

    public List<FriendDto> getFriends(String account) {
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        final List<FriendDto> list = new ArrayList<FriendDto>();
        FutureTask<Integer> t1 = this.getFirstFriends(list, account);
        FutureTask<Integer> t2 = this.getSecondFriends(list, account);
        FutureTask<Integer> t3 = this.getThirdFriends(list, account);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);
        ApiGateway.syncwait(tasks);
        return list;
    }

    protected List<FriendDto> assembleData(List<Map<String, Object>> list) {
        List<FriendDto> dtos = new ArrayList<FriendDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        FriendDto dto = null;
        for (Map<String, Object> map : list) {
            dto = JsonUtil.map2Object(map, FriendDto.class);
            String account = null != dto ? dto.getAccount() : "";
            String lastLoginTime = memberLogService.getLastLoginTime(account);
            dto.setLastLoginTime(lastLoginTime);
            dtos.add(dto);
        }
        return dtos;
    }

    protected FutureTask<Integer> getFirstFriends(final List<FriendDto> list, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                List<Map<String, Object>> friends = memberCenterDao.getFirstFriends(account);
                if (CollectionUtils.isEmpty(friends)) {
                    return 0;
                }
                synchronized (list) {
                    List<FriendDto> dtos = assembleData(friends);
                    list.addAll(dtos);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getSecondFriends(final List<FriendDto> list, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                List<Map<String, Object>> friends = memberCenterDao.getSecondFriends(account);
                if (CollectionUtils.isEmpty(friends)) {
                    return 0;
                }
                synchronized (list) {
                    List<FriendDto> dtos = assembleData(friends);
                    list.addAll(dtos);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getThirdFriends(final List<FriendDto> list, final String account) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                List<Map<String, Object>> friends = memberCenterDao.getSecondFriends(account);
                if (CollectionUtils.isEmpty(friends)) {
                    return 0;
                }
                synchronized (list) {
                    List<FriendDto> dtos = assembleData(friends);
                    list.addAll(dtos);
                }
                return 1;
            }
        });
        return futureTask;
    }

    public MemberExtProfileDto getExtData(String account, Integer source, String ip) {
        MemberExtProfileDto extProfileDto = new MemberExtProfileDto();
        String identity = PlayerCode._MEMBER.getCode();
        MemberProfileDto profileDto = this.getProfile(account, ip);
        String grade = profileDto.getGradeCode();
        Integer vip = profileDto.getVip();
        String json = memberMessgeService.getUnreadMsg(account, source, identity, grade, vip);
        UnReadMsgDto msgDto = StringUtils.isEmpty(json) ? null : JsonUtil.string2Object(json, UnReadMsgDto.class);
        extProfileDto.setUserBaseInfo(profileDto);
        extProfileDto.setUnreadMsgAmounts(msgDto);
        return extProfileDto;
    }

    public MemberProfileDto getProfile(String account, String ip) {
        MemberProfileDto dto = null;
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return null;
        }
        dto = new MemberProfileDto();
        BeanUtils.copyProperties(player, dto);
        Integer vip = null != player.getVip() ? player.getVip() : 0;
        String vipTitle = vipRuleManager.getVipTitle(vip);
        BigDecimal balance = null != player.getBalance() ? player.getBalance() : BigDecimal.ZERO;
        Boolean isCompleteTradePwd = StringUtils.isEmpty(player.getTradePwd()) ? Boolean.FALSE : Boolean.TRUE;
        dto.setBalance(balance);
        dto.setVip(vip);
        dto.setTitle(vipTitle);
        dto.setIsCompleteTradePwd(isCompleteTradePwd);
        dto.setIp(ip);
        dto.setCity(IPUtils.getCity(ip));
        return dto;
    }

    public void saveProfile(String account, MemberProfileDto dto, String ip) {
        Member player = memberCenterDao.findByAccount(account);
        if (null == player) {
            return;
        }
        String realName = dto.getRealName();
        String mobile = dto.getMobile();
        String qq = dto.getQq();
        String wechat = dto.getWechat();
        String birthday = dto.getBirthday();
        if (!StringUtils.isEmpty(realName)) {
            player.setRealName(realName);
            String originl = player.getRealName();
            if (!originl.equals(realName)) {
                String loginType = LogTypeEnum._U_REALNAME.getType();
                memberLogService.saveEventLog(account, loginType, originl, realName, IPUtils.ipToLong(ip));
            }
        }
        if (!StringUtils.isEmpty(qq)) {
            player.setQq(qq);
            String originl = player.getQq();
            if (!originl.equals(qq)) {
                String loginType = LogTypeEnum._U_QQ.getType();
                memberLogService.saveEventLog(account, loginType, originl, qq, IPUtils.ipToLong(ip));
            }
        }
        if (!StringUtils.isEmpty(wechat)) {
            player.setWechat(wechat);
            String originl = player.getWechat();
            if (!originl.equals(wechat)) {
                String loginType = LogTypeEnum._U_WECHAT.getType();
                memberLogService.saveEventLog(account, loginType, originl, wechat, IPUtils.ipToLong(ip));
            }
        }
        player.setBirthday(StringUtils.isEmpty(birthday) ? player.getBirthday() : birthday);
        memberCenterDao.save(player);
    }

    public void updateLoginPwd(String account, String password, String ip) {
        Member Player = this.findByUserName(account);
        if (null == Player) {
            return;
        }
        String encodePwd = new BCryptPasswordEncoder().encode(password);
        String loginType = LogTypeEnum._U_LOGIN_PWD.getType();
        Player.setLoginPwd(encodePwd);
        memberCenterDao.save(Player);
        memberLogService.saveEventLog(account, loginType, "", password, IPUtils.ipToLong(ip));
    }

    public void updateMobile(String account, String mobile, String ip) {
        Member Player = this.findByUserName(account);
        if (null == Player) {
            return;
        }
        String originl = Player.getMobile();
        String loginType = LogTypeEnum._U_MOBILE.getType();
        Player.setMobile(mobile);
        memberCenterDao.save(Player);
        memberLogService.saveEventLog(account, loginType, originl, mobile, IPUtils.ipToLong(ip));
    }

    public void updateRealName(String account, String realName, String ip) {
        Member Player = this.findByUserName(account);
        if (null == Player) {
            return;
        }
        String originl = Player.getRealName();
        String loginType = LogTypeEnum._U_REALNAME.getType();
        Player.setRealName(realName);
        memberCenterDao.save(Player);
        memberLogService.saveEventLog(account, loginType, originl, realName, IPUtils.ipToLong(ip));
    }

    public void updateEmail(String account, String email, String ip) {
        Member Player = this.findByUserName(account);
        if (null == Player) {
            return;
        }
        String originl = Player.getEmail();
        String loginType = LogTypeEnum._U_EMAIL.getType();
        Player.setEmail(email);
        memberCenterDao.save(Player);
        memberLogService.saveEventLog(account, loginType, originl, email, IPUtils.ipToLong(ip));
    }

    public void updateTradePwd(String account, String password, String ip) {
        Member player = this.findByUserName(account);
        if (null == player) {
            return;
        }
        String originl = "";
        String logType = LogTypeEnum._U_TRADE_PWD.getType();
        String encodePwd = new BCryptPasswordEncoder().encode(password);
        player.setTradePwd(encodePwd);
        memberCenterDao.save(player);
        memberLogService.saveEventLog(account, logType, originl, password, IPUtils.ipToLong(ip));
    }

    public BigDecimal updateBalance(String account, BigDecimal amount) {
        BigDecimal balance = BigDecimal.ZERO;
        try {
            redisLock.lock(account, account);
            Member player = memberCenterDao.findByAccount(account);
            if (null == player) {
                return balance;
            }
            balance = null != player.getBalance() ? player.getBalance().add(amount) : balance;
            player.setBalance(balance);
            memberCenterDao.save(player);
        } finally {
            redisLock.unlock(account, account);
        }
        return balance;
    }

    public BigDecimal updateBalanceAndInterest(String account, BigDecimal amount, BigDecimal interest) {
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal interestBalance = BigDecimal.ZERO;
        try {
            redisLock.lock(account, account);
            Member player = memberCenterDao.findByAccount(account);
            if (null == player) {
                return balance;
            }
            balance = null != player.getBalance() ? player.getBalance().add(amount) : balance;
            interestBalance = null != player.getInterestBalance() ? player.getInterestBalance().add(interest) : interestBalance;
            player.setBalance(balance);
            player.setInterestBalance(interestBalance);
            memberCenterDao.save(player);
        } finally {
            redisLock.unlock(account, account);
        }
        return balance;
    }

    @LcnTransaction
    public String createMemberDrawOrder(String drawSchemeCode, Integer vip, Integer source, String account, BigDecimal amount, String bankRealName, String bankName, String bankAccount, String ip) {
        BigDecimal costAmount = drawSchemeManager.getDrawCharge(drawSchemeCode, vip);
        Integer okStatus = OrderCode._PENDING.getCode();
        Integer auditStatus = WorkFlow.Draw._PENDIGN_AUDIT.getCode();
        String remarks = String.format(DRAW, account);
        BigDecimal balance = this.updateBalance(account, amount.negate());
        String orderNo = memberOrderService.saveDrawOrder(okStatus, auditStatus, costAmount, source, account, amount, balance, bankRealName, bankName, bankAccount, ip, remarks, true);
        return orderNo;
    }

    public JSONObject createMemberDepositOrder(Integer source, String account, BigDecimal amount, Long channelId, String ip) {
        DepositChannel channel = depositChannelManager.getChannelRule(channelId);
        String realName = this.getRealName(account);
        String bankRealName = channel.getBankRealName();
        String bankAccount = channel.getBankAccount();
        String payType = channel.getPayType();
        String channelType = channel.getChannelType();
        String channelName = channel.getChannelName();
        String remarks = String.format(DEPOSIT, account, NumberUtils.hideName(realName));
        Integer okStatus = OrderCode._PENDING.getCode();
        BigDecimal balance = this.getBalance(account);
        JSONObject result = memberOrderService.saveDepositOrder(bankRealName, bankAccount, payType, channelType, channelName, okStatus, source, account, amount, balance, ip, remarks);
        return result;
    }
}

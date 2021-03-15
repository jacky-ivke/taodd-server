package com.esports.patner.service;

import com.esports.api.center.MemberService;
import com.esports.api.order.MemberOrderService;
import com.esports.constant.CapitalCode;
import com.esports.constant.GlobalCode;
import com.esports.constant.PlayerCode;
import com.esports.patner.bean.db1.PartnerOrder;
import com.esports.patner.dao.db1.PartnerOrderDao;
import com.esports.patner.dto.*;
import com.esports.processor.ApiGateway;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.PageData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class PartnerManager {

    @Autowired
    private PartnerOrderDao partnerOrderDao;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberOrderService memberOrderService;


    public boolean checkAccount(String account) {
        boolean success = memberService.checkAccount(account);
        return success;
    }

    public void createAccount(String account, String password, String inviter, Integer source, String ip) {
        memberService.createAccount(account, password, inviter, source, ip);
    }

    public PartnerProfileDto getProfile(String account) {
        PartnerProfileDto dto = null;
        String json = memberService.getAccount(account);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        dto = JsonUtil.string2Object(json, PartnerProfileDto.class);
        BigDecimal predictAmount = this.getCurrentInvitePredictAmount(account);
        Integer betFriends = this.getValidFriends(account);
        FriendsBetDto friendsBetDto = this.getFriendsBetTotalAmount(account);
        BigDecimal firstBetAmount = null != friendsBetDto && null != friendsBetDto.getFirstBetTotalAmount() ? friendsBetDto.getFirstBetTotalAmount() : BigDecimal.ZERO;
        BigDecimal secondBetAmount = null != friendsBetDto && null != friendsBetDto.getSecondBetTotalAmount() ? friendsBetDto.getSecondBetTotalAmount() : BigDecimal.ZERO;
        BigDecimal thirdBetAmount = null != friendsBetDto && null != friendsBetDto.getThirdBetTotalAmount() ? friendsBetDto.getThirdBetTotalAmount() : BigDecimal.ZERO;
        BigDecimal betTotalAmount = null != friendsBetDto && null != friendsBetDto.getBetTotalAmount() ? friendsBetDto.getBetTotalAmount() : BigDecimal.ZERO;
        BigDecimal inviteTotalBalance = null != dto.getInviteBalance() ? dto.getInviteBalance() : BigDecimal.ZERO;
        dto.setFriends(betFriends);
        dto.setFirstBetTotalAmount(firstBetAmount);
        dto.setSecondBetTotalAmount(secondBetAmount);
        dto.setThirdBetTotalAmount(thirdBetAmount);
        dto.setFriendsBetTotaAmount(betTotalAmount);
        dto.setInviteTotalBalance(inviteTotalBalance);
        dto.setInviteBalance(predictAmount);
        dto.setH5exclusiveLinks(this.getPartnerLinks(dto.getH5exclusiveLinks(), dto.getInviteCode()));
        dto.setPcexclusiveLinks(this.getPartnerLinks(dto.getPcexclusiveLinks(), dto.getInviteCode()));
        return dto;
    }

    private String getPartnerLinks(String url, String inviteCode) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String links = url + "?inviteCode=" + inviteCode;
        if (url.indexOf("?") >= 0) {
            String params = "&inviteCode=" + inviteCode;
            links = url + params;
        }
        return links;
    }

    public BigDecimal getCurrentInvitePredictAmount(String account) {
        BigDecimal award = BigDecimal.ZERO;
        String type = CapitalCode._INVITATION.getCode();
        String startTime = memberOrderService.getLastAwardTime(account, type);
        String endTime = DateUtils.getCurrentTime();
        Map<String, Object> map = partnerOrderDao.getPartnerFriendsBetSummary(account, startTime, endTime);
        FriendsBetDto dto = !CollectionUtils.isEmpty(map) ? JsonUtil.map2Object(map, FriendsBetDto.class) : null;
        award = null != dto && null != dto.getInviteCommissionAmount() ? dto.getInviteCommissionAmount() : award;
        return award;
    }

    public Integer getValidFriends(String account) {
        Integer friends = 0;
        List<FriendDto> list = this.getFriends(account);
        if(CollectionUtils.isEmpty(list)){
            return friends;
        }
        Iterator<FriendDto> iterator = list.iterator();
        while (iterator.hasNext()) {
            FriendDto friend = iterator.next();
            BigDecimal betTotalAmount = memberOrderService.getBetTotalAmount(friend.getAccount());
            if (null != betTotalAmount && betTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
                friends++;
            }
        }
        return friends;
    }

    private List<FriendDto> getFriends(String account) {
        String arr = memberService.getFriends(account);
        if (StringUtils.isEmpty(arr)) {
            return null;
        }
        List<FriendDto> dtos = JsonUtil.string2List(arr, FriendDto.class);
        return dtos;
    }

    public PageData getFriends(String account, Integer page, Integer pageSize) {
        List<FriendDto> list = this.getFriends(account);
        PageData pageData = PageData.startPage(list, 0, page, pageSize);
        return pageData;
    }

    public FriendsBetDto getFriendsBetTotalAmount(String account) {
        Map<String, Object> map = partnerOrderDao.getPartnerFriendsBetSummary(account, null, null);
        FriendsBetDto dto = !CollectionUtils.isEmpty(map) ? JsonUtil.map2Object(map, FriendsBetDto.class) : null;
        return dto;
    }

    public JSONObject getPartnerFriendsBetRecords(String account, Integer page, Integer pageSize, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getPartnerFriendsBetSummary(json, account, startTime, endTime);
        FutureTask<Integer> t2 = this.getPartnerFriendsBetOrders(json, account, page, pageSize, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }


    public PageData getPartnerOrders(String account, Integer page, Integer pageSize, String startTime, String endTime) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<PartnerOrder> spec = new Specification<PartnerOrder>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PartnerOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                //增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                if (!StringUtils.isEmpty(account)) {
                    predicate.getExpressions().add(cb.equal(root.get("account").as(String.class), account));
                }
                if (!StringUtils.isEmpty(startTime)) {
                    Timestamp start = DateUtils.getDayStartTime(DateUtils.stringFormatDate(startTime));
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createTime").as(Timestamp.class), start));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    Timestamp end = DateUtils.getDayEndTime(DateUtils.stringFormatDate(endTime));
                    predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createTime").as(Timestamp.class), end));
                }
                return predicate;
            }
        };
        Page<PartnerOrder> pages = partnerOrderDao.findAll(spec, pageable);
        PageData pageData = PageData.builder(pages);
        List<PartnerBillDto> dtos = this.assembleFriendsBetData((List<PartnerOrder>) pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    public List<PartnerBillDto> assembleFriendsBetData(List<PartnerOrder> list) {
        List<PartnerBillDto> dtos = new ArrayList<PartnerBillDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        PartnerBillDto dto = null;
        Iterator<PartnerOrder> iterator = list.iterator();
        while (iterator.hasNext()) {
            PartnerOrder order = iterator.next();
            dto = new PartnerBillDto();
            BigDecimal betAmount = null == order.getBetAmount() ? BigDecimal.ZERO : order.getBetAmount();
            BigDecimal point = null == order.getPercentage() ? BigDecimal.ZERO : order.getPercentage();
            BigDecimal commissionAmount = betAmount.multiply(point.divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_DOWN);
            dto.setCreateTime(order.getCreateTime());
            dto.setFriend(order.getFriend());
            dto.setType(PlayerCode.getRemarks(order.getType()));
            dto.setCommissionAmount(commissionAmount);
            dto.setBetAmount(betAmount);
            dto.setPoint(point);
            dtos.add(dto);
        }
        return dtos;
    }

    protected FutureTask<Integer> getPartnerFriendsBetSummary(final JSONObject json, final String account, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String, Object> map = partnerOrderDao.getPartnerFriendsBetSummary(account, startTime, endTime);
                if (CollectionUtils.isEmpty(map)) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(map);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getPartnerFriendsBetOrders(final JSONObject json, final String account, final Integer page, final Integer pageSize, final String startTime, String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getPartnerOrders(account, page, pageSize, startTime, endTime);
                if (null == pageData) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(JsonUtil.object2Map(pageData));
                }
                return 1;
            }
        });
        return futureTask;
    }

    public JSONObject getPartnerCommissionRecords(String account, Integer page, Integer pageSize, String startTime, String endTime) {
        startTime = DateUtils.getDayStartTime(startTime);
        endTime = DateUtils.getDayEndTime(endTime);
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        JSONObject json = new JSONObject();
        FutureTask<Integer> t1 = this.getPartnerCommissionSummary(json, account, startTime, endTime);
        FutureTask<Integer> t2 = this.getPartnerCommissionOrders(json, account, page, pageSize, startTime, endTime);
        tasks.add(t1);
        tasks.add(t2);
        ApiGateway.syncwait(tasks);
        return json;
    }

    public PageData getPartnerCommissionOrders(String account, Integer page, Integer pageSize, String startTime, String endTime) {
        String type = CapitalCode._INVITATION.getCode();
        String json = memberOrderService.getActivityOrders(account, null, type, startTime, endTime, page, pageSize);
        PageData pageData = JsonUtil.string2Object(json, PageData.class);
        List<PartnerCommissionDto> dtos = this.assemblePartnerCommissionData(pageData.getContents());
        pageData.setContents(dtos);
        return pageData;
    }

    public List<PartnerCommissionDto> assemblePartnerCommissionData(List<?> list) {
        List<PartnerCommissionDto> dtos = new ArrayList<PartnerCommissionDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        String data = JsonUtil.object2String(list);
        dtos = JsonUtil.string2List(data, PartnerCommissionDto.class);
        return dtos;
    }

    protected FutureTask<Integer> getPartnerCommissionSummary(final JSONObject json, final String account, final String startTime, final String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                String type = CapitalCode._INVITATION.getCode();
                BigDecimal totalAmount = memberOrderService.getActivityAmount(account, type, startTime, endTime);
                synchronized (json) {
                    json.put("totalAmount", totalAmount);
                }
                return 1;
            }
        });
        return futureTask;
    }

    protected FutureTask<Integer> getPartnerCommissionOrders(final JSONObject json, final String account, final Integer page, final Integer pageSize, final String startTime, String endTime) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                PageData pageData = getPartnerCommissionOrders(account, page, pageSize, startTime, endTime);
                if (null == pageData) {
                    return 0;
                }
                synchronized (json) {
                    json.putAll(JsonUtil.object2Map(pageData));
                }
                return 1;
            }
        });
        return futureTask;
    }

}

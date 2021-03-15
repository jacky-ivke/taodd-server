package com.esports.external.basic.service;

import com.esports.constant.GlobalCode;
import com.esports.external.basic.bean.db1.AppType;
import com.esports.external.basic.bean.db1.ExternalApp;
import com.esports.external.basic.dao.db1.ExternalAppDao;
import com.esports.external.basic.dto.PlatformDto;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.ProxyFactory;
import com.esports.external.handler.RespResultDTO;
import com.esports.processor.ApiGateway;
import com.esports.utils.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


@Service
public class ExternalAppManager {

    @Autowired
    private ExternalAppDao externalAppDao;

    @Autowired
    private TransferOrderManager transferOrderManager;

    public boolean checkAppStatus(String apiCode) {
        boolean success = true;
        Integer okStatus = GlobalCode._ENABLE.getCode();
        ExternalApp app = externalAppDao.findByAppCodeAndOkStatus(apiCode, okStatus);
        if (null == app) {
            success = false;
        }
        return success;
    }

    public PageData getApiList(String gameType, Integer page, Integer pageSize) {
        Sort.Order[] orders = {Sort.Order.desc("id"), Sort.Order.desc("createTime")};
        Sort sort = Sort.by(Arrays.asList(orders));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Specification<ExternalApp> spec = new Specification<ExternalApp>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ExternalApp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 增加筛选条件
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("okStatus").as(Integer.class), GlobalCode._ENABLE.getCode()));
                if (!StringUtils.isEmpty(gameType)) {
                    Subquery<String> sq = query.subquery(String.class);
                    Root<AppType> manager = sq.from(AppType.class);
                    sq.select(manager.get("apiCode"));
                    sq.where(cb.equal(manager.get("isUsed").as(Boolean.class), Boolean.TRUE),
                            cb.equal(manager.get("apiCode").as(String.class), root.get("appCode").as(String.class)),
                            cb.equal(manager.get("gameType").as(String.class), gameType));
                    predicate.getExpressions().add(cb.exists(sq));
                }
                return predicate;
            }
        };
        Page<ExternalApp> pages = externalAppDao.findAll(spec, pageable);
        List<PlatformDto> list = this.assembleData(pages);
        PageData pageData = PageData.builder(pages);
        pageData.setContents(list);
        return pageData;
    }

    protected List<PlatformDto> assembleData(Page<ExternalApp> pages) {
        List<PlatformDto> dtos = new ArrayList<PlatformDto>();
        List<ExternalApp> list = pages.getContent();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<ExternalApp> it = list.iterator();
        PlatformDto dto = null;
        while (it.hasNext()) {
            dto = new PlatformDto();
            ExternalApp app = it.next();
            dto.setApiCode(app.getAppCode());
            dto.setName(app.getTitle());
            dto.setBalance(BigDecimal.ZERO);
            dtos.add(dto);
        }
        return dtos;
    }

    protected List<PlatformDto> assembleData(final String account, final String ip, List<PlatformDto> list) {
        List<PlatformDto> dtos = new ArrayList<PlatformDto>();
        if (CollectionUtils.isEmpty(list)) {
            return dtos;
        }
        Iterator<PlatformDto> it = list.iterator();
        PlatformDto dto = null;
        List<FutureTask<PlatformDto>> tasks = new ArrayList<FutureTask<PlatformDto>>();
        while (it.hasNext()) {
            PlatformDto externalApp = it.next();
            final String apiCode = externalApp.getApiCode();
            final String title = externalApp.getName();
            FutureTask<PlatformDto> futureTask = new FutureTask<PlatformDto>(new Callable<PlatformDto>() {
                @Override
                public PlatformDto call() throws Exception {
                    BigDecimal platformBalance = getProxyBalance(account, apiCode, ip);
                    PlatformDto dto = new PlatformDto();
                    dto.setApiCode(apiCode);
                    dto.setName(title);
                    dto.setBalance(platformBalance);
                    return dto;
                }
            });
            tasks.add(futureTask);
        }
        dtos = ApiGateway.syncwait(tasks);
        return dtos;
    }

    public PageData getApiDetail(String account, String ip) {
        PageData pageData = this.getApiList(null, 0, 100);
        List<PlatformDto> list = this.assembleData(account, ip, (List<PlatformDto>) pageData.getContents());
        pageData.setContents(list);
        return pageData;
    }

    public BigDecimal getProxyBalance(String account, String apiCode, String ip) {
        AbstractTemplate template = ProxyFactory.getProxy(apiCode);
        RespResultDTO dto = template.balance(account, ip);
        BigDecimal platformBalance = null != dto && dto.getSuccess() ? new BigDecimal(dto.getData().toString()) : BigDecimal.ZERO;
        return platformBalance;
    }

    public void checkTransferStatus(String apiCode, String account, String orderNo, BigDecimal amount, String ip, Integer lang, Long timestamp) {
        if (null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if ("BBIN".equalsIgnoreCase(apiCode) && amount.intValue() <= 0) {
            return;
        }
        AbstractTemplate template = ProxyFactory.getProxy(apiCode);
        RespResultDTO dto = template.searchOrder(account, orderNo, lang, timestamp);
        BigDecimal actualAmount = AbstractTemplate.getActualAmount(amount, dto);
        if (null != dto && null != dto.getSuccess() && dto.getSuccess().booleanValue()) {
            transferOrderManager.depositSuccess(orderNo, orderNo, account, apiCode, actualAmount, ip, account);
        } else if (null != dto && null != dto.getSuccess() && !dto.getSuccess().booleanValue()) {
            transferOrderManager.depositFaild(orderNo, orderNo, dto.getCode(), dto.getMessage(), account, apiCode, actualAmount, ip);
        }
    }

    public void oneKeyRecovery(final String account, final Integer deviceType, final Integer lang, final String ip) {
        List<String> transferPlats = transferOrderManager.getTransferPlats(account);
        if (CollectionUtils.isEmpty(transferPlats)) {
            return;
        }
        List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
        for (final String apiCode : transferPlats) {
            FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    BigDecimal balance = getProxyBalance(account, apiCode, ip);
                    transferOrderManager.draw(account, apiCode, lang, deviceType, balance, ip);
                    return 1;
                }
            });
            tasks.add(futureTask);
        }
        ApiGateway.syncwait(tasks);
    }

    public BigDecimal getApiTotalBalance(String account, String ip) {
        BigDecimal totalBalance = BigDecimal.ZERO;
        List<String> transferPlats = transferOrderManager.getTransferPlats(account);
        if (CollectionUtils.isEmpty(transferPlats)) {
            return totalBalance;
        }
        List<FutureTask<BigDecimal>> tasks = new ArrayList<FutureTask<BigDecimal>>();
        for (final String apiCode : transferPlats) {
            FutureTask<BigDecimal> futureTask = new FutureTask<BigDecimal>(new Callable<BigDecimal>() {
                @Override
                public BigDecimal call() throws Exception {
                    BigDecimal balance = getProxyBalance(account, apiCode, ip);
                    return balance;
                }
            });
            tasks.add(futureTask);
        }
        ApiGateway.start(tasks);
        for (FutureTask<BigDecimal> future : tasks) {
            try {
                BigDecimal apiBalance = future.get();
                if (null != apiBalance) {
                    totalBalance = totalBalance.add(apiBalance);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return totalBalance;
    }
}

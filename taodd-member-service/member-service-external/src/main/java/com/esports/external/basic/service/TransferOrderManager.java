package com.esports.external.basic.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.esports.api.center.MemberService;
import com.esports.api.log.MemberLogService;
import com.esports.constant.CapitalCode;
import com.esports.constant.GlobalCode;
import com.esports.constant.OrderCode;
import com.esports.external.basic.bean.db1.TransferOrder;
import com.esports.external.basic.dao.db1.TransferOrderDao;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.ProxyFactory;
import com.esports.external.handler.RespResultDTO;
import com.esports.utils.IPUtils;
import com.esports.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class TransferOrderManager {

    private static final String DEPOSIT_SUCCESS = "【一键转入】操作者：%s";

    private static final String DEPOSIT_FAILD = "【一键转入】操作者：%s 失败原因：%s";

    private static final String DRAW_SUCCESS = "【一键转出】操作者：%s";

    private static final String DRAW_FAILD = "【一键转出】操作者：%s 失败原因：%s";

    private static final String CENTER_WALLET = "center";

    @Autowired
    private TransferOrderDao transferOrderDao;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLogService memberLogService;


    public TransferOrder createOrder(Integer okStatus, String type, String orderNo, String transactionId, String account, BigDecimal amount, BigDecimal wallet, String from, String to, String ip, String remarks) {
        TransferOrder order = new TransferOrder();
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setOrderNo(orderNo);
        order.setType(type);
        order.setTransactionId(transactionId);
        order.setAmount(amount);
        order.setBalance(wallet);
        order.setAccount(account);
        order.setExpenditure(from);
        order.setRevenue(to);
        order.setSource(GlobalCode._PC.getCode());
        order.setIp(IPUtils.ipToLong(ip));
        order.setOkStatus(okStatus);
        order.setRemarks(remarks);
        transferOrderDao.save(order);
        return order;
    }

    public BigDecimal deposit(String account, Integer deviceType, Integer lang, String proxyCode, BigDecimal amount, String ip) {
        BigDecimal balance = BigDecimal.ZERO;
        if (null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return balance;
        }
        String orderNo = RandomUtil.getUUID("");
        Long timestamp = System.currentTimeMillis();
        AbstractTemplate template = ProxyFactory.getProxy(proxyCode);
        try {
            RespResultDTO dto = template.deposit(account, lang, orderNo, amount, deviceType, ip, timestamp);
            String transactionId = "";
            if (null == dto || !dto.getSuccess()) {
                balance = this.depositFaild(orderNo, transactionId, dto.getCode(), dto.getMessage(), account, proxyCode, amount, ip);
            } else {
                balance = this.depositSuccess(orderNo, transactionId, account, proxyCode, amount, ip, "");
            }
        } catch (Exception e) {
            log.error("【一键转入失败】 玩家:{}, 转入平台：{}, 转入金额：{}, 错误信息：{}", account, proxyCode, amount, e.getMessage());
            balance = BigDecimal.ZERO;
        }
        return balance;
    }

    @LcnTransaction
    public BigDecimal depositSuccess(String orderNo, String transactionId, String account, String proxyCode, BigDecimal amount, String ip, String opUser) {
        AbstractTemplate template = ProxyFactory.getProxy(proxyCode);
        BigDecimal balance = BigDecimal.ZERO;
        Integer okStatus = OrderCode._SUCCESS.getCode();
        RespResultDTO dto = template.balance(account, ip);
        BigDecimal wallet = null != dto && null != dto.getData() ? new BigDecimal(dto.getData().toString()) : BigDecimal.ZERO;
        String from = CENTER_WALLET;
        String to = proxyCode;
        String remarks = String.format(DEPOSIT_SUCCESS, opUser);
        String type = CapitalCode._DEPOSIT.getCode();
        String secondType = CapitalCode._DEPOSIT_ONEKAY.getCode();
        this.createOrder(okStatus, type, orderNo, transactionId, account, amount, wallet, from, to, ip, remarks);
        balance = memberService.updateBalance(account, amount.negate());
        memberLogService.saveTradeLog(account, type, secondType, okStatus, amount, orderNo, balance, ip, remarks, proxyCode);
        return balance;
    }

    @LcnTransaction
    public BigDecimal depositFaild(String orderNo, String transactionId, String errorCode, String errorMsg, String account, String proxyCode, BigDecimal amount, String ip) {
        AbstractTemplate template = ProxyFactory.getProxy(proxyCode);
        BigDecimal balance = BigDecimal.ZERO;
        Integer okStatus = OrderCode._FAILED.getCode();
        RespResultDTO dto = template.balance(account, ip);
        BigDecimal wallet = null != dto && null != dto.getData() ? new BigDecimal(dto.getData().toString()) : BigDecimal.ZERO;
        String from = CENTER_WALLET;
        String to = proxyCode;
        String remarks = String.format(DEPOSIT_FAILD, account, errorMsg);
        String type = CapitalCode._DEPOSIT.getCode();
        String secondType = CapitalCode._DEPOSIT_ONEKAY.getCode();
        this.createOrder(okStatus, type, orderNo, transactionId, account, amount, wallet, from, to, ip, remarks);
        balance = memberService.getBalance(account);
        memberLogService.saveTradeLog(account, type, secondType, okStatus, amount, orderNo, balance, ip, remarks, proxyCode);
        return balance;
    }

    @LcnTransaction
    public BigDecimal draw(String account, String proxyCode, Integer lang, Integer deviceType, BigDecimal amount, String ip) {
        BigDecimal balance = BigDecimal.ZERO;
        if (null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return balance;
        }
        if ("BBIN".equalsIgnoreCase(proxyCode) && amount.intValue() <= 0) {
            return balance;
        }
        String orderNo = RandomUtil.getUUID("");
        Long timestamp = System.currentTimeMillis();
        AbstractTemplate template = ProxyFactory.getProxy(proxyCode);
        try {
            RespResultDTO dto = template.draw(account, lang, orderNo, amount, deviceType, ip, timestamp);
            String transactionId = "";
            BigDecimal actualAmount = AbstractTemplate.getActualAmount(amount, dto);
            if (null == dto || !dto.getSuccess()) {
                balance = this.drawFaild(orderNo, transactionId, dto.getCode(), dto.getMessage(), account, proxyCode, actualAmount, ip);
            } else {
                balance = this.drawSuccess(orderNo, transactionId, account, proxyCode, actualAmount, ip);
            }
        } catch (Exception e) {
            log.error("【一键转出失败】 玩家:{}, 转出平台：{}, 转出金额：{}, 错误信息：{}", account, proxyCode, amount, e.getMessage());
            balance = null;
        }
        return balance;
    }

    @LcnTransaction
    public BigDecimal drawSuccess(String orderNo, String transactionId, String account, String proxyCode, BigDecimal amount, String ip) {
        AbstractTemplate template = ProxyFactory.getProxy(proxyCode);
        BigDecimal balance = BigDecimal.ZERO;
        Integer okStatus = OrderCode._SUCCESS.getCode();
        RespResultDTO dto = template.balance(account, ip);
        BigDecimal wallet = null != dto && null != dto.getData() ? new BigDecimal(dto.getData().toString()) : BigDecimal.ZERO;
        String from = proxyCode;
        String to = CENTER_WALLET;
        String remarks = String.format(DRAW_SUCCESS, account);
        String type = CapitalCode._DRAW.getCode();
        String secondType = CapitalCode._DRAW_ONEKAY.getCode();
        this.createOrder(okStatus, type, orderNo, transactionId, account, amount, wallet, from, to, ip, remarks);
        balance = memberService.updateBalance(account, amount);
        memberLogService.saveTradeLog(account, type, secondType, okStatus, amount, orderNo, balance, ip, remarks, proxyCode);
        return balance;
    }

    @LcnTransaction
    public BigDecimal drawFaild(String orderNo, String transactionId, String errorCode, String errorMsg, String account, String proxyCode, BigDecimal amount, String ip) {
        AbstractTemplate template = ProxyFactory.getProxy(proxyCode);
        BigDecimal balance = BigDecimal.ZERO;
        Integer okStatus = OrderCode._FAILED.getCode();
        RespResultDTO dto = template.balance(account, ip);
        BigDecimal wallet = null != dto && null != dto.getData() ? new BigDecimal(dto.getData().toString()) : BigDecimal.ZERO;
        String from = proxyCode;
        String to = CENTER_WALLET;
        String remarks = String.format(DRAW_FAILD, account, errorMsg);
        String type = CapitalCode._DRAW.getCode();
        String secondType = CapitalCode._DRAW_ONEKAY.getCode();
        this.createOrder(okStatus, type, orderNo, transactionId, account, amount, wallet, from, to, ip, remarks);
        balance = memberService.getBalance(account);
        memberLogService.saveTradeLog(account, type, secondType, okStatus, amount, orderNo, balance, ip, remarks, proxyCode);
        return balance;
    }

    public String getLastTransferPlat(String account) {
        String proxyCode = transferOrderDao.getLastTransferPlat(account);
        return proxyCode;
    }

    public List<String> getTransferPlats(String account) {
        List<String> list = transferOrderDao.getTransferPlats(account);
        return list;
    }

}

package com.esports.external.handler.yabo.lottery;


import com.esports.constant.GlobalSourceCode;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.yabo.lottery.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.Md5Utils;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class YaboLotteryProxy extends AbstractTemplate {

    private final static String TYPE = "YABOCP";

    /**
     * 1、会员  3、测试会员
     */
    private final static Integer MEMBER_TYPE = 1;

    private final static String BALANCE_KEY = "amount";

    private final static String TOKEN_KEY = "token";

    private final static String ERROR_KEY = "code";

    private final static String DATA_KEY = "data";

    private final static String SIGN_KEY = "sign";

    private final static String ORDER_LIST_KEY = "list";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final static Integer TRANSFER_IN = 1;

    private final static Integer TRANSFER_OUT = 2;

    private static final Integer PAGE_SIZE = 100;

    private final ProxyConfigManager proxyConfigManager;

    public YaboLotteryProxy() {
        proxyConfigManager = (ProxyConfigManager) SpringUtil.getBean("proxyConfigManager");
    }

    protected boolean check(RespResultDTO result, String code) {
        boolean success = false;
        if (ObjectUtils.isEmpty(result) || StringUtils.isEmpty(code)) {
            return success;
        }
        String ok = result.getCode();
        if (code.equals(ok)) {
            success = true;
        }
        return success;
    }

    protected ProxyConfig getProxyConfig(String apiType) {
        String merchantType = MerchantCode._YABO.getCode();
        String type = TYPE;
        ProxyConfig config = proxyConfigManager.getConfig(merchantType, type, apiType);
        return config;
    }

    private String sign(SortedMap<String, Object> sortMap, String md5Key) {
        Set<String> keySet = sortMap.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuilder source = new StringBuilder();
        while (iter.hasNext()) {
            String key = iter.next();
            source.append(key).append(sortMap.get(key));
        }
        StringBuffer sb = new StringBuffer(source).append(md5Key);
        String sign = Md5Utils.getMD5(sb.toString()).toLowerCase();
        return sign;
    }

    protected RespResultDTO buildResult(String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(LotteryCode.getMessage(errorCode));
        return respResult;
    }

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !this.isJsonObject(json)) {
            return respResult;
        }
        JSONObject jsonObj = JSONObject.fromObject(json);
        String code = null != jsonObj && jsonObj.containsKey(ERROR_KEY) ? jsonObj.getString(ERROR_KEY) : "";
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = LotteryCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(LotteryCode.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return null;
        }
        String data = obj.containsKey(DATA_KEY) ? obj.getString(DATA_KEY) : "";
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        if (StringUtils.isEmpty(dataKey)) {
            return data;
        }
        Object dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey) ? JSONObject.fromObject(data).getString(dataKey) : data;
        return dataValue;
    }

    protected String encryption(Object object, ProxyConfig config) {
        String md5Key = config.getMd5key();
        SortedMap<String, Object> sortMap = JsonUtil.object2Map(object);
        String sign = this.sign(sortMap, md5Key);
        sortMap.put(SIGN_KEY, sign);
        String params = JsonUtil.map2String(sortMap);
        return params;
    }

    protected String json2UrlParams(ProxyConfig config, Object dto) throws Exception {
        SortedMap<String, Object> map = JsonUtil.object2Map(dto);
        String reqUrl = this.map2UrlParams(config, map);
        return reqUrl;
    }

    protected String map2UrlParams(ProxyConfig config, SortedMap<String, Object> map) {
        String url = config.getRquestUrl();
        StringBuffer urlParams = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = null != entry.getValue() ? entry.getValue() : "";
            urlParams.append(key).append("=").append(value).append("&");
        }
        urlParams.deleteCharAt(urlParams.length() - 1);
        String reqUrl = new StringBuffer(url).append(urlParams).toString();
        return reqUrl;
    }

    public Map<String, Object> getHeaders() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        String params = this.encryption(dto, config);
        String url = config.getRquestUrl();
        String json = HttpClientUtils.post(url, params, null);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO handlerGet(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) {
        String params = this.encryption(dto, config);
        SortedMap<String, Object> map = JsonUtil.string2Map(params);
        String url = config.getRquestUrl();
        String json = HttpClientUtils.get(url, this.getHeaders(), map);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    public String unFormatLoginName(String loginName) {
        ProxyConfig config = this.getProxyConfig("history");
        String prefix = null != config ? config.getMerchantCode() : "";
        if (!StringUtils.isEmpty(prefix)) {
            loginName = loginName.replace(prefix, "");
        }
        return loginName;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setMember(loginName);
            dto.setMemberType(MEMBER_TYPE);
            dto.setPassword(LotteryCode._DEFAULT_PWD.getCode());
            dto.setMerchant(config.getMerchantCode());
            dto.setTimestamp(System.currentTimeMillis());
            respResult = this.handler(dto, config, null);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, LotteryCode._ACCOUNT_EXISTS.getCode());
            if (success) {
                respResult.setCode(String.valueOf(HttpStatus.SC_OK));
            }
            log.info("【玩家注册】平台：YABOCP,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(LotteryCode._EXCEPTION.getCode());
            log.error("【玩家注册】平台YABOCP,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            amount = formatAmount(amount);
            dto.setMember(loginName);
            dto.setTransferType(TRANSFER_IN);
            dto.setMerchantAccount(config.getMerchantCode());
            dto.setAmount(amount);
            dto.setNotifyId(orderNo);
            dto.setTimestamp(System.currentTimeMillis());
            respResult = this.handler(dto, config, null);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, LotteryCode._TRANSFER_ACCOUNT_ERROR.getCode());
            if (!success) {
                return respResult;
            }
            // 玩家额度由商户转移至平台，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
            }
            log.info("【转账上分】平台：YABOCP,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账上分】平台：YABOCP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("draw");
            if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            amount = formatAmount(amount);
            dto.setMember(loginName);
            dto.setTransferType(TRANSFER_OUT);
            dto.setMerchantAccount(config.getMerchantCode());
            dto.setAmount(amount);
            dto.setNotifyId(orderNo);
            dto.setTimestamp(System.currentTimeMillis());
            respResult = this.handler(dto, config, null);
            log.info("【转账下分】平台：YABOCP,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：YABOCP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setMember(loginName);
            dto.setMerchant(config.getMerchantCode().trim());
            dto.setTimestamp(System.currentTimeMillis());
            respResult = this.handler(dto, config, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, LotteryCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：YABOCP,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(LotteryCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：YABOCP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            String source = GlobalSourceCode._PC.getCode();
            if (source.equals(deviceType.toString())) {
                respResult = this.playDesktopGame(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
            } else {
                respResult = this.playMobileGame(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
            }
        } catch (Exception e) {
            respResult = this.buildResult(LotteryCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTIBC,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO playMobileGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("mobileGame");
        if (null == config) {
            return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
        }
        respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    protected RespResultDTO playDesktopGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("desktopGame");
        if (null == config) {
            return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
        }
        respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    private RespResultDTO openGame(ProxyConfig config, String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {

        if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
        }

        RespResultDTO respResult = this.loginGame(loginName, memberIp, lang);
        boolean success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (!success) {
            return respResult;
        }
        String token = respResult.getData().toString();
        ReqOpenGameDTO dto = new ReqOpenGameDTO();
        dto.setToken(token);
        dto.setLotteryId(gameCode);
        String reqUrl = this.json2UrlParams(config, dto);
        respResult.setData(reqUrl);
        log.info("【进入游戏】平台：YABOCP, 账号：{}, 结果：{}", loginName, respResult);
        return respResult;
    }

    protected RespResultDTO loginGame(String loginName, String memberIp, Integer lang) {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("loginServer");
        if (null == config || StringUtils.isEmpty(loginName)) {
            return respResult;
        }
        ReqLoginDTO dto = new ReqLoginDTO();
        dto.setMember(loginName);
        dto.setPassword(LotteryCode._DEFAULT_PWD.getCode());
        dto.setMerchant(config.getMerchantCode());
        dto.setTimestamp(System.currentTimeMillis());
        SortedMap<String, Object> sortMap = JsonUtil.object2Map(dto);
        String sign = this.sign(sortMap, config.getMd5key());
        sortMap.put("sign", sign);
        sortMap.put("loginIp", memberIp);
        sortMap.put("returnUrl", "");
        String url = config.getRquestUrl();
        String json = HttpClientUtils.post(url, this.getHeaders(), sortMap);
        respResult = this.adapter(json, TOKEN_KEY);
        log.info("【登录游戏】平台：YABOCP, 账号：{}, 结果：{}", loginName, respResult);
        boolean success = this.check(respResult, LotteryCode._LOGIN_ERROR.getCode());
        if (!success) {
            return respResult;
        }
        respResult = this.create(loginName, lang);
        success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (success) {
            respResult = loginGame(loginName, memberIp, lang);
            return respResult;
        }
        return respResult;
    }

    @Override
    public RespResultDTO history(String startTime, String endTime, Long lastOrderId) {
        long start = System.currentTimeMillis();
        long end = start;
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("history");
            if (null == config) {
                return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
            }
            ReqBetRecordHistoryDTO dto = new ReqBetRecordHistoryDTO();
            dto.setStartTime(startTime);
            dto.setEndTime(endTime);
            dto.setMerchantAccount(config.getMerchantCode());
            dto.setAgency(Boolean.TRUE);
            dto.setLastOrderId(lastOrderId);
            dto.setPageSize(PAGE_SIZE);
            respResult = this.handlerGet(dto, config, null);
            end = System.currentTimeMillis();
            log.info("【游戏记录】平台：YABOCP 开始时间：{}, 结束时间：{}, 开始订单号：{}, 耗时:{},结果：{}", startTime, endTime, lastOrderId, (end - start), respResult);
        } catch (Exception e) {
            respResult = this.buildResult(LotteryCode._EXCEPTION.getCode());
            log.error("【游戏记录】平台：YABOCP 开始时间：{}, 结束时间:{}, 开始订单号：{}, 耗时:{},异常信息：{}", startTime, endTime, lastOrderId, (end - start), e.getMessage());
        }
        return respResult;
    }

    private RespResultDTO checkTransfer(RespResultDTO respResult, String loginName, Integer lang, String orderNo, BigDecimal amount, Long timestamp) {
        if (null == respResult || !respResult.getSuccess() || (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData()))) {
            return respResult;
        }
        RespResultDTO dto = this.searchOrder(loginName, orderNo, lang, timestamp);
        BigDecimal transferAmount = null == dto.getData() ? formatAmount(amount) : new BigDecimal(dto.getData().toString()).abs();
        dto.setData(transferAmount);
        return dto;
    }

    @Override
    public RespResultDTO searchOrder(String loginName, String orderNo, Integer lang, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("searchOrder");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(LotteryCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setMember(loginName);
            dto.setMerchant(config.getMerchantCode().trim());
            dto.setTimestamp(timestamp);
            dto.setNotifyId(orderNo);
            respResult = this.handler(dto, config, ORDER_LIST_KEY);
            String data = null != respResult.getData() ? respResult.getData().toString() : "";
            String errorCode = respResult.getCode();
            if (StringUtils.isEmpty(data) || !isJsonArry(data) || JSONArray.fromObject(data).size() < 1) {
                errorCode = StringUtils.isEmpty(errorCode) ? LotteryCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            respResult.setData(this.getTransferAmount(data));
            log.info("【转账查询】平台：YABOCP,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(LotteryCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：YABO-CP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    private BigDecimal getTransferAmount(String data) {
        BigDecimal amount = BigDecimal.ZERO;
        if (!isJsonArry(data)) {
            return amount;
        }
        JSONArray arr = JSONArray.fromObject(data);
        if (null == arr) {
            return amount;
        }
        JSONObject json = arr.getJSONObject(0);
        if (null == json) {
            return amount;
        }
        amount = json.containsKey(ORDER_AMOUNT_KEY) ? new BigDecimal(json.getString(ORDER_AMOUNT_KEY)) : amount;
        return amount;
    }
}

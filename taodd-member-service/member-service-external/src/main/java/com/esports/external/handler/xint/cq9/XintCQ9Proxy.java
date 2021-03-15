package com.esports.external.handler.xint.cq9;


import com.esports.constant.GameTypeCode;
import com.esports.constant.GlobalSourceCode;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.cq9.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
public class XintCQ9Proxy extends AbstractTemplate {

    private final static String TYPE = "CQ9";

    private final static String DATA_KEY = "data";

    private final static String ERROR_KEY = "status";

    private final static String ERROR_CODE_KEY = "code";

    private final static String BALANCE_KEY = "balance";

    private final static String TOKEN_KEY = "usertoken";

    private final static String GAME_URL_KEY = "url";

    private final static String NOT_DATA_KEY = "null";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final ProxyConfigManager proxyConfigManager;

    public XintCQ9Proxy() {
        proxyConfigManager = (ProxyConfigManager) SpringUtil.getBean("proxyConfigManager");
    }

    protected ProxyConfig getProxyConfig(String apiType) {
        String merchantType = MerchantCode._XT.getCode();
        String type = TYPE;
        ProxyConfig config = proxyConfigManager.getConfig(merchantType, type, apiType);
        return config;
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

    protected RespResultDTO buildResult(String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(XintCQ9Code.getMessage(errorCode));
        return respResult;
    }

    protected Map<String, Object> getHeaders(ProxyConfig config) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Authorization", config.getAesKey());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        Map<String, Object> map = JsonUtil.object2Map(dto);
        String url = config.getRquestUrl();
        String json = HttpClientUtils.post(url, this.getHeaders(config), map);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
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

    protected RespResultDTO handlerGet(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {

        SortedMap<String, Object> map = JsonUtil.object2Map(dto);
        String reqUrl = this.map2UrlParams(config, map);
        String json = HttpClientUtils.get(reqUrl, this.getHeaders(config), null);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
            return respResult;
        }
        String code = this.getCode(json);
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = XintCQ9Code._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintCQ9Code.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        Object dataValue = null;
        if (null == obj) {
            return dataValue;
        }
        String data = obj.containsKey(DATA_KEY) ? obj.getString(DATA_KEY) : (obj.containsKey(dataKey) ? obj.getString(dataKey) : "");
        if (StringUtils.isEmpty(data)) {
            dataValue = json;
        }
        if (StringUtils.isEmpty(dataKey)) {
            dataValue = data;
        }
        dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey) ? JSONObject.fromObject(data).getString(dataKey) : data;
        return (NOT_DATA_KEY.equalsIgnoreCase(dataValue.toString()) ? "" : dataValue);
    }

    protected String getCode(String json) {
        String code = "0";
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return code;
        }
        JSONObject error = obj.containsKey(ERROR_KEY) ? obj.getJSONObject(ERROR_KEY) : null;
        if (null == error) {
            return code;
        }
        code = error.containsKey(ERROR_CODE_KEY) ? error.getString(ERROR_CODE_KEY) : code;
        return code;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setAccount(loginName);
            dto.setNickname(loginName);
            dto.setPassword(XintCQ9Code._DEFAULT_PWD.getCode());
            respResult = this.handler(dto, config, null, XintCQ9Code._ACCOUNT_EXISTS.getCode());
            log.info("【创建账号】平台：XINTCQ9,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintCQ9Code._EXCEPTION.getCode());
            log.error("【创建账号】平台:XINTCQ9,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    private RespResultDTO loginGame(String loginName, Integer lang) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("loginServer");
        if (null == config) {
            return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
        }
        ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
        dto.setAccount(loginName);
        dto.setPassword(XintCQ9Code._DEFAULT_PWD.getCode());
        respResult = this.handler(dto, config, TOKEN_KEY);
        if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
            return respResult;
        }
        boolean success = this.check(respResult, XintCQ9Code._ACCOUNT_PASSWORD_ERROR.getCode());
        if (!success) {
            return respResult;
        }
        // 玩家额度由商户转移至平台，如果账号不存在则新建账号
        respResult = this.create(loginName, lang);
        success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (success) {
            respResult = this.loginGame(loginName, lang);
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setAccount(loginName);
            String json = HttpClientUtils.get(config.getRquestUrl() + loginName, this.getHeaders(config), null);
            respResult = this.adapter(json, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, XintCQ9Code._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTCQ9,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintCQ9Code._EXCEPTION.getCode());
            log.error("【获取余额】平台:XINTCQ9,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setAccount(loginName);
            dto.setAmount(formatAmount(amount));
            dto.setMtcode(orderNo);
            respResult = this.handler(dto, config, null);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintCQ9Code._ACCOUNT_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
                return respResult;
            }
            log.info("【转账上分】平台：XINTCQ9,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台:XINTCQ9,账号：{}, 异常信息:{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, formatAmount(amount), timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("draw");
            if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setAccount(loginName);
            dto.setAmount(formatAmount(amount));
            dto.setMtcode(orderNo);
            respResult = this.handler(dto, config, null);
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
            log.info("【转账下分】平台：XINTCQ9,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台:XINTCQ9,账号：{}, 异常信息:{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, formatAmount(amount), timestamp);
        }
        return respResult;
    }

    private RespResultDTO checkTransfer(RespResultDTO respResult, String loginName, Integer lang, String orderNo, BigDecimal amount, Long timestamp) {
        if (null == respResult || !respResult.getSuccess() || (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData()))) {
            return respResult;
        }
        RespResultDTO dto = this.searchOrder(loginName, orderNo, lang, timestamp);
        BigDecimal transferAmount = null == dto.getData() ? amount : new BigDecimal(dto.getData().toString()).abs();
        dto.setData(transferAmount);
        return dto;
    }

    @Override
    public RespResultDTO searchOrder(String loginName, String orderNo, Integer lang, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("searchOrder");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
            }
            String json = HttpClientUtils.get(config.getRquestUrl() + orderNo, this.getHeaders(config), null);
            respResult = this.adapter(json, ORDER_AMOUNT_KEY);
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || ObjectUtils.isEmpty(respResult.getData())) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintCQ9Code._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            log.info("【转账查询】平台：XINTCQ9,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintCQ9Code._EXCEPTION.getCode());
            log.error("【转账查询】平台:XINTCQ9,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            if (StringUtils.isEmpty(gameCode)) {
                if (GameTypeCode._TABLE_GAME.getCode().equalsIgnoreCase(gameType)) {
                    respResult = this.playPokerGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
                } else {
                    respResult = this.playSlotsGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
                }
            } else {
                respResult = this.playOneGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            }
            log.info("【进入游戏】平台：XINTCQ9,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintCQ9Code._EXCEPTION.getCode());
            log.error("【进入游戏】平台:XINTCQ9,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    public RespResultDTO playSlotsGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        ProxyConfig config = this.getProxyConfig("playSlotsGame");
        if (null == config || StringUtils.isEmpty(loginName)) {
            return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
        }
        RespResultDTO respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    public RespResultDTO playPokerGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        ProxyConfig config = this.getProxyConfig("playPokerGame");
        if (null == config || StringUtils.isEmpty(loginName)) {
            return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
        }
        RespResultDTO respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    public RespResultDTO playOneGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        ProxyConfig config = this.getProxyConfig("playOneGame");
        if (null == config || StringUtils.isEmpty(loginName)) {
            return this.buildResult(XintCQ9Code._PARAM_ERROR.getCode());
        }
        RespResultDTO respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    private RespResultDTO openGame(ProxyConfig config, String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = this.loginGame(loginName, lang);
        boolean success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (!success) {
            return respResult;
        }
        //金额大于 0 时，视为期望同时带入余额，会校验金额和转账单号
        if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
        }
        String token = respResult.getData().toString();
        String language = LanguageCode.getLanguage(lang);
        String source = GlobalSourceCode._PC.getCode();
        String gameplat = source.equals(deviceType.toString()) ? "web" : "mobile";
        ReqOpenGameDTO dto = new ReqOpenGameDTO();
        dto.setUsertoken(token);
        dto.setLang(language);
        dto.setGamecode(gameCode);
        dto.setGameplat(gameplat);
        respResult = this.handler(dto, config, GAME_URL_KEY);
        return respResult;
    }
}

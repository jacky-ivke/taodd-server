package com.esports.external.handler.xint.ia;

import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.ia.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class XintIaProxy extends AbstractTemplate {

    private final static String TYPE = "IA";

    private final static String DATA_KEY = "data";

    private final static String ERROR_KEY = "error";

    private final static String ERROR_CODE_KEY = "code";

    private final static String BALANCE_KEY = "balance";

    private final static String GAME_URL_KEY = "game_url";

    private final static String ORDER_LIST_KEY = "translogs";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final ProxyConfigManager proxyConfigManager;

    public XintIaProxy() {
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
        respResult.setMessage(XintIaCode.getMessage(errorCode));
        return respResult;
    }

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
            return respResult;
        }
        String code = this.getCode(json);
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = XintIaCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintIaCode.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected String getCode(String json) {
        String code = XintIaCode._SUCCESS.getCode();
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

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return null;
        }
        String data = obj.containsKey(DATA_KEY) ? obj.getString(DATA_KEY) : (obj.containsKey(dataKey) ? obj.getString(dataKey) : "");
        if (StringUtils.isEmpty(data)) {
            return json;
        }
        if (StringUtils.isEmpty(dataKey)) {
            return data;
        }
        Object dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey) ? JSONObject.fromObject(data).getString(dataKey) : data;
        return dataValue;
    }

    protected Map<String, Object> getHeaders() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        String url = config.getRquestUrl();
        Map<String, Object> params = JsonUtil.object2Map(dto);
        String json = HttpClientUtils.post(url, this.getHeaders(), params);
        RespResultDTO respResult = this.adapter(json, dataKey);
        return respResult;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintIaCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setLoginName(loginName);
            dto.setSecretKey(config.getAesKey().trim());
            dto.setOperatorToken(config.getMd5key().trim());
            respResult = this.handler(dto, config, null, XintIaCode._ACCOUNT_EXISTS.getCode());
            log.info("【玩家注册】平台：XINTIA,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintIaCode._EXCEPTION.getCode());
            log.error("【玩家注册】平台: XINTIA,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config) {
                return this.buildResult(XintIaCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setLoginName(loginName);
            dto.setSecretKey(config.getAesKey().trim());
            dto.setOperatorToken(config.getMd5key().trim());
            respResult = this.handler(dto, config, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, XintIaCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTIA,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintIaCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintIaCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setLoginName(loginName);
            dto.setSecretKey(config.getAesKey().trim());
            dto.setOperatorToken(config.getMd5key().trim());
            dto.setTraceId(orderNo);
            dto.setAmount(formatAmount(amount));
            respResult = this.handler(dto, config, null);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintIaCode._ACCOUNT_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
            }
            log.info("【转账上分】平台：XINTIA,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账上分】平台：XINTIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintIaCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setLoginName(loginName);
            dto.setSecretKey(config.getAesKey().trim());
            dto.setOperatorToken(config.getMd5key().trim());
            dto.setTraceId(orderNo);
            dto.setAmount(formatAmount(amount));
            respResult = this.handler(dto, config, null);
            log.info("【转账下分】平台：XINTIA,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：XINTIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintIaCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setLoginName(loginName);
            dto.setSecretKey(config.getAesKey().trim());
            dto.setOperatorToken(config.getMd5key().trim());
            dto.setTraceId(orderNo);
            respResult = this.handler(dto, config, ORDER_LIST_KEY);
            boolean success = this.check(respResult, XintIaCode._DATA_NOT_EXISTS.getCode());
            String errorCode = respResult.getCode();
            if (success) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintIaCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String data = null != respResult.getData() ? respResult.getData().toString() : "";
            respResult.setData(this.getTransferAmount(data));
            log.info("【转账查询】平台：XINTIA,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintIaCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playGame");
            if (null == config) {
                return this.buildResult(XintIaCode._PARAM_ERROR.getCode());
            }
            if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            log.info("【进入游戏】平台：XINTIA, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintIaCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTIA,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO openGame(ProxyConfig config, String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
        String language = LanguageCode.getLanguage(lang);
        dto.setLoginName(loginName);
        dto.setSecretKey(config.getAesKey().trim());
        dto.setOperatorToken(config.getMd5key().trim());
        dto.setGameCode(dto.getPlatform(deviceType));
        dto.setLanguage(language);
        respResult = this.handler(dto, config, GAME_URL_KEY);
        if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
            return respResult;
        }
        boolean success = this.check(respResult, XintIaCode._ACCOUNT_NOT_EXISTS.getCode());
        if (!success) {
            return respResult;
        }
        respResult = this.create(loginName, lang);
        success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (success) {
            respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
        }
        return respResult;
    }
}

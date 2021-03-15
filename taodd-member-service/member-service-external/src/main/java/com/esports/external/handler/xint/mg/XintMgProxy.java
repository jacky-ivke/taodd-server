package com.esports.external.handler.xint.mg;

import com.esports.constant.GlobalSourceCode;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.mg.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.Base64Utils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class XintMgProxy extends AbstractTemplate {

    private final static String TYPE = "MG";

    private final static String BALANCE_KEY = "credit_balance";

    private final static String DATA_KEY = "data";

    private final static String ERROR_KEY = "error";

    private final static String ERROR_CODE_KEY = "code";

    private final static String TRANSFER_OUT = "DEBIT";

    private final static String TRANSFER_IN = "CREDIT";

    private final static String PC_APP_ID = "1001";

    private final static String H5_APP_ID = "1002";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final static String ACCESS_TOKEN_KEY = "access_token";

    private final ProxyConfigManager proxyConfigManager;

    public XintMgProxy() {
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
        respResult.setMessage(XintMgCode.getMessage(errorCode));
        return respResult;
    }

    protected Map<String, Object> getGetMethodHeaders(String auth, Integer lang) {
        Map<String, Object> headers = new HashMap<String, Object>();
        try {
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            headers.put("Authorization", auth);
            headers.put("X-DAS-TX-ID", uuid);
            headers.put("X-DAS-CURRENCY", "CNY");
            headers.put("X-DAS-TZ", "UTC");
            headers.put("X-DAS-LANG", language);
            headers.put("Content-Type", "application/x-www-form-urlencoded");
        } catch (Exception e) {
            headers = Collections.emptyMap();
        }
        return headers;
    }

    protected Map<String, Object> getPostMethodHeaders(String auth, Integer lang) {
        Map<String, Object> headers = new HashMap<String, Object>();
        try {
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            headers.put("Authorization", auth);
            headers.put("X-DAS-TX-ID", uuid);
            headers.put("X-DAS-CURRENCY", "CNY");
            headers.put("X-DAS-TZ", "UTC");
            headers.put("X-DAS-LANG", language);
        } catch (Exception e) {
            headers = Collections.emptyMap();
        }
        return headers;
    }

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
            return respResult;
        }
        String code = this.getCode(json);
        String ok = XintMgCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintMgCode.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected String getCode(String json) {
        String code = XintMgCode._SUCCESS.getCode();
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
        data = isJsonArry(data) ? JSONArray.fromObject(data).getString(0) : data;
        Object dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey) ? JSONObject.fromObject(data).getString(dataKey) : data;
        return dataValue;
    }

    protected RespResultDTO handlerJsonParams(Object dto, String token, ProxyConfig config, String dataKey, Integer lang, String... ignoreCodes) throws Exception {
        String url = config.getRquestUrl();
        String params = JsonUtil.object2String(dto);
        String json = HttpClientUtils.post(url, params, this.getPostMethodHeaders(token, lang));
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO handlerFormParams(Object dto, String token, ProxyConfig config, String dataKey, Integer lang, String... ignoreCodes) throws Exception {
        String url = config.getRquestUrl();
        Map<String, Object> map = JsonUtil.object2Map(dto);
        String json = HttpClientUtils.post(url, this.getPostMethodHeaders(token, lang), map);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO handlerGet(Object dto, String token, ProxyConfig config, String dataKey, Integer lang, String... ignoreCodes) throws Exception {
        String url = config.getRquestUrl();
        Map<String, Object> map = JsonUtil.object2Map(dto);
        String json = HttpClientUtils.get(url, this.getGetMethodHeaders(token, lang), map);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    private String getOauthToken(ProxyConfig config) {
        String authorization = "";
        try {
            String apiUsr = config.getAesKey();
            String apiPwd = config.getMd5key();
            String auth = new StringBuffer(apiUsr).append(":").append(apiPwd).toString();
            String base64 = Base64Utils.encode(auth.getBytes(StandardCharsets.UTF_8));
            authorization = new StringBuffer("Basic").append(" ").append(base64).toString();
        } catch (Exception e) {
        }
        return authorization;
    }

    private String getAccessToken(String token) {
        String authorization = "";
        try {
            authorization = new StringBuffer("Bearer").append(" ").append(token).toString();
        } catch (Exception e) {
        }
        return authorization;
    }

    private RespResultDTO authorization() {
        RespResultDTO respResult = new RespResultDTO();
        try {
            ProxyConfig config = this.getProxyConfig("auth");
            if (null == config) {
                return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
            }
            ReqAuthDTO dto = new ReqAuthDTO();
            String token = this.getOauthToken(config);
            respResult = this.handlerFormParams(dto, token, config, ACCESS_TOKEN_KEY, null);
            String accessToken = this.getAccessToken(respResult.getData().toString());
            respResult.setData(accessToken);
            log.info("【登入认证】平台：XINTMG,结果：{}", respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintMgCode._EXCEPTION.getCode());
            log.error("【登入认证】平台:XINTMG, 异常信息:{}", e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            RespResultDTO auth = this.authorization();
            String accessToken = auth.getData().toString();
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setPartenId(config.getMerchantCode().trim());
            dto.setUserName(loginName);
            dto.setPassword(XintMgCode._DEFAULT_PWD.getCode());
            dto.setExtRef(loginName);
            respResult = this.handlerJsonParams(dto, accessToken, config, null, lang, XintMgCode._ACCOUNT_EXISTS.getCode());
            log.info("【创建账号】平台：XINTMG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintMgCode._EXCEPTION.getCode());
            log.error("【创建账号】平台:XINTMG,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            RespResultDTO auth = this.authorization();
            String accessToken = auth.getData().toString();
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setLoginName(loginName);
            respResult = this.handlerGet(dto, accessToken, config, BALANCE_KEY, null);
            respResult = this.buildBalanceResult(respResult, XintMgCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTMG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintMgCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTMG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            RespResultDTO auth = this.authorization();
            String accessToken = auth.getData().toString();
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo)
                    || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setLoginName(loginName);
            dto.setType(TRANSFER_IN);
            dto.setOrderNo(orderNo);
            dto.setAmount(formatAmount(amount));
            respResult = this.handlerJsonParams(Arrays.asList(dto), accessToken, config, null, lang);
            log.info("【转账上分】平台：XINTMG,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintMgCode._ACCOUNT_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            //玩家登录游戏，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
            }
        } catch (Exception e) {
            log.error("【转账上分】平台：XINTMG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            RespResultDTO auth = this.authorization();
            String accessToken = auth.getData().toString();
            ProxyConfig config = this.getProxyConfig("draw");
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo)
                    || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setLoginName(loginName);
            dto.setType(TRANSFER_OUT);
            dto.setOrderNo(orderNo);
            dto.setAmount(formatAmount(amount));
            respResult = this.handlerJsonParams(Arrays.asList(dto), accessToken, config, null, lang);
            log.info("【转账下分】平台：XINTMG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：XINTMG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
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
            RespResultDTO auth = this.authorization();
            String accessToken = auth.getData().toString();
            ProxyConfig config = this.getProxyConfig("searchOrder");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setLoginName(loginName);
            dto.setOrderNo(orderNo);
            respResult = this.handlerGet(dto, accessToken, config, null, lang);
            String data = null != respResult.getData() ? respResult.getData().toString() : "";
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || StringUtils.isEmpty(data) || !isJsonArry(data) || JSONArray.fromObject(data).size() < 1) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintMgCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String source = respResult.getSource();
            respResult.setData(this.getTransferAmount(source));
            log.info("【转账查询】平台：XINTMG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintMgCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTMG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    private BigDecimal getTransferAmount(String data) {
        BigDecimal amount = BigDecimal.ZERO;
        if (!isJsonObject(data)) {
            return amount;
        }
        JSONObject json = JSONObject.fromObject(data);
        if (null == json) {
            return amount;
        }
        JSONArray arr = json.containsKey(DATA_KEY) ? json.getJSONArray(DATA_KEY) : null;
        if (null == arr) {
            return amount;
        }
        amount = null != arr.getJSONObject(0) && arr.getJSONObject(0).containsKey(ORDER_AMOUNT_KEY) ? new BigDecimal(arr.getJSONObject(0).getString(ORDER_AMOUNT_KEY)) : amount;
        return amount;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            respResult = this.openGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            log.info("【进入游戏】平台：XINTMG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintMgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTMG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO openGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        RespResultDTO auth = this.authorization();
        String accessToken = auth.getData().toString();
        ProxyConfig config = this.getProxyConfig("playGame");
        if (null == config) {
            return this.buildResult(XintMgCode._PARAM_ERROR.getCode());
        }
        ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
        String language = LanguageCode.getLanguage(lang);
        String appId = GlobalSourceCode._PC.getCode().equals(String.valueOf(deviceType)) ? PC_APP_ID : H5_APP_ID;
        dto.setLoginName(loginName);
        dto.setGameId(gameCode);
        dto.setAppId(appId);
        dto.setLang(language);
        dto.setTitanium("default");
        respResult = this.handlerJsonParams(dto, accessToken, config, null, lang);
        if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
            return respResult;
        }
        boolean success = this.check(respResult, XintMgCode._ACCOUNT_NOT_EXISTS.getCode());
        if (!success) {
            return respResult;
        }
        //玩家登录游戏，如果账号不存在则新建账号
        respResult = this.create(loginName, lang);
        success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (success) {
            respResult = this.openGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
        }
        return respResult;
    }
}

package com.esports.external.handler.xint.ibc;


import com.esports.constant.GlobalSourceCode;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.ibc.dto.*;
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
import java.util.SortedMap;

@Slf4j
public class XintIbcProxy extends AbstractTemplate {

    private final static String TYPE = "IBC";

    private final static Integer WALLET_ID = 1;

    private final static Integer TRANSFER_IN = 1;

    private final static Integer TRANSFER_OUT = 0;

    private final static String BALANCE_KEY = "balance";

    private final static String ERROR_KEY = "error_code";

    private final static String DATA_KEY = "Data";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final static String PREFIX_KEY = "AOYING_ZHP_";

    private final ProxyConfigManager proxyConfigManager;

    private static final BigDecimal MIN_TRANSFER_AMOUNT = new BigDecimal("1");

    private static final BigDecimal MAX_TRANSFER_AMOUNT = new BigDecimal("100000");

    public XintIbcProxy() {
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

    protected Map<String, Object> getHeaders() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    protected RespResultDTO buildResult(XintIbcCode xintIbcCode, String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(XintIbcCode.getMessage(errorCode, xintIbcCode));
        return respResult;
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

    protected RespResultDTO adapter(XintIbcCode xintIbcCode, String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !this.isJsonObject(json)) {
            return respResult;
        }
        JSONObject jsonObj = JSONObject.fromObject(json);
        String statusCode = null != jsonObj && jsonObj.containsKey(ERROR_KEY) ? jsonObj.getString(ERROR_KEY) : "";
        String code = this.getCode(statusCode, json);
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = XintIbcCode.ErrorType.Common._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(this.getMessage(code, xintIbcCode));
        return respResult;
    }

    private JSONObject getResultData(String data) {
        JSONObject obj = null;
        if (isJsonObject(data)) {
            return JSONObject.fromObject(data);
        } else if (isJsonArry(data)) {
            JSONArray arr = JSONArray.fromObject(data);
            if (null != arr && arr.size() > 0) {
                obj = arr.getJSONObject(0);
            }
        }
        return obj;
    }

    protected String getCode(String statusCode, String json) {
        //由于各个接口返回状态码不统一,先获取接口业务本身返回错误代码，如果获取不到（成功），则取外层的状态码作为本次请求的响应结果
        JSONObject obj = JSONObject.fromObject(json);
        String errorCode = obj.containsKey(ERROR_KEY) && !StringUtils.isEmpty(obj.getString(ERROR_KEY)) ? obj.getString(ERROR_KEY) : statusCode;
        return errorCode;
    }

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return null;
        }
        String value = obj.containsKey(DATA_KEY) ? obj.getString(DATA_KEY) : "";
        //由于返回格式不统一,部分接口返回JSON数组部分接口返回JSON,统一返回格式
        JSONObject data = this.getResultData(value);
        if (null == data) {
            return value;
        }
        if (StringUtils.isEmpty(dataKey)) {
            return data;
        }
        Object dataValue = data.containsKey(dataKey) ? data.getString(dataKey) : "";
        return dataValue;
    }

    protected String getMessage(String errorCode, XintIbcCode xintIbcCode) {
        //由于接口错误代码分为statusCode、errorCode,现根据错误代码获取自身业务返回信息，如获取不到，则更具状态码获取状态提示信息
        String msg = XintIbcCode.getMessage(errorCode, xintIbcCode);
        if (StringUtils.isEmpty(errorCode)) {
            xintIbcCode = XintIbcCode.Common;
            msg = XintIbcCode.getMessage(errorCode, xintIbcCode);
        }
        return msg;
    }

    protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey, XintIbcCode xintIbcCode, String... ignoreCodes) throws Exception {
        SortedMap<String, Object> sortedMap = JsonUtil.object2Map(dto);
        String url = config.getRquestUrl();
        String json = HttpClientUtils.post(url, this.getHeaders(), sortedMap);
        RespResultDTO respResult = this.adapter(xintIbcCode, json, dataKey, ignoreCodes);
        return respResult;
    }

    protected static String formatLoginName(String loginName) {
        String prefix = PREFIX_KEY;
        StringBuffer sb = new StringBuffer(prefix).append(loginName);
        return sb.toString();
    }

    public static String unformatLoginName(String loginName) {
        String prefix = PREFIX_KEY;
        loginName = loginName.replace(prefix, "");
        return loginName;
    }

    protected String createOrderId(String orderNo) {
        StringBuffer sb = new StringBuffer(PREFIX_KEY).append(orderNo);
        return sb.toString();
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setVendorId(config.getMerchantCode().trim());
            dto.setMemberId(XintIbcProxy.formatLoginName(loginName));
            dto.setUserName(XintIbcProxy.formatLoginName(loginName));
            dto.setMinTransfer(MIN_TRANSFER_AMOUNT);
            dto.setMaxTransfer(MAX_TRANSFER_AMOUNT);
            dto.setCurrency(20);
            respResult = this.handler(dto, config, null, XintIbcCode.Create, XintIbcCode.ErrorType.Create._CREATE_VENDOR_MEMBER_ID_REPEAT.getCode());
        } catch (Exception e) {
            respResult = this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._EXCEPTION.getCode());
            log.error("【玩家注册】平台:XINTIBC,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setVendorId(config.getMerchantCode().trim());
            dto.setMemberId(XintIbcProxy.formatLoginName(loginName));
            dto.setWalletId(WALLET_ID);
            respResult = this.handler(dto, config, BALANCE_KEY, XintIbcCode.Balance);
            respResult = this.buildBalanceResult(respResult, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
            log.info("【获取余额】平台：XINTIBC,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTIBC, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo)
                    || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setVendorId(config.getMerchantCode().trim());
            dto.setMemberId(XintIbcProxy.formatLoginName(loginName));
            dto.setOrderNo(this.createOrderId(orderNo));
            dto.setAmount(formatAmount(amount));
            dto.setCurrency(20);
            dto.setDirection(TRANSFER_IN);
            dto.setWalletId(WALLET_ID);
            respResult = this.handler(dto, config, null, XintIbcCode.Transfer);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            log.info("【转账上分】平台：XINTIBC, 账号：{}, 结果：{}", loginName, respResult);
            boolean success = this.check(respResult, XintIbcCode.ErrorType.Transfer._TRANSER_ACCOUNT_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
                return respResult;
            }
        } catch (Exception e) {
            log.error("【转账上分】平台：XINTIBC, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo)
                    || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setVendorId(config.getMerchantCode().trim());
            dto.setMemberId(XintIbcProxy.formatLoginName(loginName));
            dto.setOrderNo(this.createOrderId(orderNo));
            dto.setAmount(amount.setScale(2, BigDecimal.ROUND_DOWN));
            dto.setCurrency(20);
            dto.setDirection(TRANSFER_OUT);
            dto.setWalletId(WALLET_ID);
            respResult = this.handler(dto, config, null, XintIbcCode.Transfer);
            log.info("【转账下分】平台：XINTIBC, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：XINTIBC, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
            ProxyConfig config = this.getProxyConfig("searchOrder");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setVendorId(config.getMerchantCode().trim());
            dto.setOrderNo(this.createOrderId(orderNo));
            dto.setWalletId(WALLET_ID);
            respResult = this.handler(dto, config, ORDER_AMOUNT_KEY, XintIbcCode.CheckFundTransfer);
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || ObjectUtils.isEmpty(respResult.getData())) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintIbcCode.ErrorType.Common._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(XintIbcCode.Common, errorCode);
            }
            log.info("【转账订单查询】平台：XINTIBC, 订单号：{}, 结果：{}", orderNo, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._EXCEPTION.getCode());
            log.error("【转账订单查询】平台：XINTIBC, 订单号：{}, 异常信息：{}", orderNo, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            String source = GlobalSourceCode._PC.getCode();
            if (source.equals(deviceType.toString())) {
                respResult = this.playDesktopGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            } else {
                respResult = this.playMobileGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            }
            log.info("【登录游戏】平台：XINTIBC, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【进入游戏】平台：XINTIBC,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO playDesktopGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        ProxyConfig config = this.getProxyConfig("desktopGame");
        if (null == config) {
            return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
        }
        RespResultDTO respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    protected RespResultDTO playMobileGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        ProxyConfig config = this.getProxyConfig("mobileGame");
        if (null == config) {
            return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
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
        if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
        }
        String token = respResult.getData().toString();
        ReqOpenGameDTO dto = new ReqOpenGameDTO();
        String language = LanguageCode.getLanguage(lang);
        dto.setToken(token);
        dto.setLang(language);
        String reqUrl = this.json2UrlParams(config, dto);
        respResult.setData(reqUrl);
        return respResult;
    }

    private RespResultDTO loginGame(String loginName, Integer lang) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("loginServer");
        if (null == config) {
            return this.buildResult(XintIbcCode.Common, XintIbcCode.ErrorType.Common._INF_ERROR.getCode());
        }
        ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
        dto.setVendorId(config.getMerchantCode());
        dto.setMemberId(XintIbcProxy.formatLoginName(loginName));
        respResult = this.handler(dto, config, null, XintIbcCode.Login);
        if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
            return respResult;
        }
        boolean success = this.check(respResult, XintIbcCode.ErrorType.Login._LOGIN_ACCOUNT_NOT_EXISTS.getCode());
        if (!success) {
            return respResult;
        }
        respResult = this.create(loginName, lang);
        success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (success) {
            respResult = loginGame(loginName, lang);
            return respResult;
        }
        return respResult;
    }
}

package com.esports.external.handler.xint.pt;


import com.esports.constant.GameTypeCode;
import com.esports.constant.GlobalSourceCode;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.pt.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
public class XintPtProxy extends AbstractTemplate {

    private final static String ENTITY_NAME = "AOYING";

    private final static String PREFIX_KEY = "AOYING_ZHP_";

    private final static String RESULT_KEY = "result";

    private final static String TYPE = "PT";

    private final static String BALANCE_KEY = "balance";

    private final static String ERROR_KEY = "errorcode";

    private final static String ORDER_STATUS_KEY = "status";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final ProxyConfigManager proxyConfigManager;

    public XintPtProxy() {
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

    public Map<String, Object> getHeards(ProxyConfig config) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("X_ENTITY_KEY", config.getAesKey());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    protected RespResultDTO buildResult(String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(XintPtCode.getMessage(errorCode));
        return respResult;
    }

    protected String json2UrlParams(Object dto, ProxyConfig config) {
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

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        if (StringUtils.isEmpty(json) || !this.isJsonObject(json)) {
            return new RespResultDTO();
        }
        RespResultDTO respResult = new RespResultDTO();
        JSONObject jsonObj = JSONObject.fromObject(json);
        String code = null != jsonObj && jsonObj.containsKey(ERROR_KEY) ? jsonObj.getString(ERROR_KEY) : "0";
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = XintPtCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintPtCode.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return null;
        }
        if (!obj.containsKey(RESULT_KEY)) {
            return null;
        }
        if (isJsonArry(obj.getString(RESULT_KEY))) {
            JSONArray arr = obj.getJSONArray(RESULT_KEY);
            if (null == arr) {
                return null;
            }
            if (StringUtils.isEmpty(dataKey)) {
                return arr;
            }
            Object dataValue = arr.getJSONObject(0).containsKey(dataKey) ? arr.getJSONObject(0).getString(dataKey) : "";
            return dataValue;
        } else if (isJsonObject(obj.getString(RESULT_KEY))) {
            JSONObject data = obj.getJSONObject(RESULT_KEY);
            if (null == data) {
                return null;
            }
            if (StringUtils.isEmpty(dataKey)) {
                return data;
            }
            Object dataValue = obj.getJSONObject(RESULT_KEY).containsKey(dataKey) ? obj.getJSONObject(RESULT_KEY).getString(dataKey) : "";
            return dataValue;
        }
        return null;
    }

    protected RespResultDTO handler(String reqUrl, Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        SortedMap<String, Object> sortedMap = JsonUtil.object2Map(dto);
        String url = StringUtils.isEmpty(reqUrl) ? config.getRquestUrl() : reqUrl;

        String filePath = new ClassPathResource("CNY.p12").getFile().getAbsolutePath();
        File file = new File(filePath);
        InputStream keyStore = new FileInputStream(file);
        String keyStorepass = config.getMd5key();
        String json = HttpClientUtils.post(url, this.getHeards(config), sortedMap, null, keyStore, keyStorepass);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected static String formatLoginName(String loginName) {
        String prefix = PREFIX_KEY;
        StringBuffer sb = new StringBuffer(prefix).append(loginName);
        return sb.toString().toUpperCase();
    }

    public static String unformatLoginName(String loginName) {
        String prefix = PREFIX_KEY;
        loginName = loginName.replace(prefix, "");
        return loginName;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setPlayername(XintPtProxy.formatLoginName(loginName));
            dto.setPassword(XintPtCode._DEFAULT_PWD.getCode());
            dto.setAdminname(config.getMerchantCode());
            dto.setEntityname(ENTITY_NAME);
            dto.setCustom02(PREFIX_KEY);
            respResult = this.handler(null, dto, config, null, XintPtCode._ACCOUNT_EXISTS.getCode());
        } catch (Exception e) {
            respResult = this.buildResult(XintPtCode._EXCEPTION.getCode());
            log.error("【玩家注册】平台:XINTPT,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setPlayername(XintPtProxy.formatLoginName(loginName));
            respResult = this.handler(null, dto, config, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, XintPtCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台:XINTPT, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintPtCode._EXCEPTION.getCode());
            log.error("【获取余额】平台:XINTPT, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setPlayername(XintPtProxy.formatLoginName(loginName));
            dto.setAmount(formatAmount(amount));
            dto.setAdminname(config.getMerchantCode());
            dto.setExternaltranid(orderNo);
            respResult = this.handler(null, dto, config, null);
            log.info("【转账上分】平台：XINTPT, 账号：{}, 结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintPtCode._ACCOUNT_NOT_EXISTS.getCode());
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
            log.error("【转账上分】平台：XINTPT, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setPlayername(XintPtProxy.formatLoginName(loginName));
            dto.setAmount(formatAmount(amount));
            dto.setAdminname(config.getMerchantCode());
            dto.setExternaltranid(orderNo);
            respResult = this.handler(null, dto, config, null);
            log.info("【转账下分】平台：XINTPT, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintPtCode._EXCEPTION.getCode());
            log.error("【转账下分】平台：XINTPT, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            String type = GameTypeCode._LIVE.getCode();
            String source = GlobalSourceCode._PC.getCode();
            if (type.equalsIgnoreCase(gameType)) {
                //live-game
                if (source.equals(deviceType.toString())) {
                    respResult = this.playLiveDesktopGame(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
                } else {
                    respResult = this.playLiveMobileGame(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
                }
            } else {
                //other-game
                if (source.equals(deviceType.toString())) {
                    respResult = this.playNgmDesktopGame(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
                } else {
                    respResult = this.playNgmMobileGame(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
                }
            }
            log.info("【进入游戏】平台：XINTPT, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintPtCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTPT,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO playLiveDesktopGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("liveDesk");
        if (null == config) {
            return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
        }
        respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    protected RespResultDTO playLiveMobileGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("liveMobile");
        if (null == config) {
            return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
        }
        respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    protected RespResultDTO playNgmDesktopGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("ngmDesk");
        if (null == config) {
            return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
        }
        respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    protected RespResultDTO playNgmMobileGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("ngmMobile");
        if (null == config) {
            return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
        }
        respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    private RespResultDTO openGame(ProxyConfig config, String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = this.create(loginName, lang);
        boolean success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
        if (!success) {
            return respResult;
        }
        if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
        }
        ReqOpenGameDTO dto = new ReqOpenGameDTO();
        String language = LanguageCode.getLanguage(lang);
        dto.setGameCode(gameCode);
        dto.setLanguage(language);
        dto.setUsername(XintPtProxy.formatLoginName(loginName));
        String reqUrl = this.json2UrlParams(dto, config);
        respResult.setData(reqUrl);
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
                return this.buildResult(XintPtCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setOrderNo(orderNo);
            respResult = this.handler(null, dto, config, ORDER_STATUS_KEY);
            log.info("【转账查询】平台：XINTPT,账号：{},结果：{}", loginName, respResult);
            String status = respResult.getData().toString();
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || !XintPtCode.OrderStatus.SUCCESS.getCode().equalsIgnoreCase(status)) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintPtCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String source = respResult.getSource();
            respResult.setData(this.getTransferAmount(source));
        } catch (Exception e) {
            respResult = this.buildResult(XintPtCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTPT, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
        JSONObject obj = json.containsKey(RESULT_KEY) ? json.getJSONObject(RESULT_KEY) : null;
        if (null == obj) {
            return amount;
        }
        amount = obj.containsKey(ORDER_AMOUNT_KEY) ? new BigDecimal(obj.getString(ORDER_AMOUNT_KEY)) : amount;
        return amount;
    }
}

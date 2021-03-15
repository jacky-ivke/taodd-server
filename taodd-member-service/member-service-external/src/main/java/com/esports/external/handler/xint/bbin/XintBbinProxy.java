package com.esports.external.handler.xint.bbin;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.bbin.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.DateUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
public class XintBbinProxy extends AbstractTemplate {

    private final static String TYPE = "BBIN";

    private final static String WEBSITE = "BETTEST";

    private final static String PREFIX_KEY = "test";

    private final static String ERROR_KEY = "Code";

    private final static String BALANCE_KEY = "Balance";

    private final static String RESULT_KEY = "result";

    private final static String DATA_KEY = "data";

    private final static String TRANSFER_IN = "IN";

    private final static String TRANSFER_OUT = "OUT";

    private final static String ORDER_AMOUNT_KEY = "Amount";

    private final static String UTC_PATTEN = "yyyy-MM-dd'T'HH:mm:ssz";

    private final static String DATE_PATTEN = "yyyy-MM-dd";

    private final static String TIME_PATTEN = "HH:mm:ss";

    private final ProxyConfigManager proxyConfigManager;


    public XintBbinProxy() {
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
        respResult.setMessage(XintBbinCode.getMessage(errorCode));
        return respResult;
    }

    protected String formatLoginName(String loginName) {
        StringBuffer sb = new StringBuffer(PREFIX_KEY).append(loginName);
        return sb.toString();
    }

    public String unFormatLoginName(String loginName) {
        String prefix = new StringBuffer(PREFIX_KEY).toString();
        loginName = loginName.replace(prefix, "");
        return loginName;
    }

    protected Map<String, Object> getHeaders(ProxyConfig config) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
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

    protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        Map<String, Object> map = JsonUtil.object2Map(dto);
        String url = config.getRquestUrl();
        String json = HttpClientUtils.post(url, this.getHeaders(config), map);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO handlerGet(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        SortedMap<String, Object> map = JsonUtil.object2Map(dto);
        String reqUrl = this.map2UrlParams(config, map);
        String json = HttpClientUtils.get(reqUrl);
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
        String ok = XintBbinCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintBbinCode.getMessage(code));
        respResult.setSource(json);
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

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return null;
        }
        String value = obj.containsKey(DATA_KEY) ? obj.getString(DATA_KEY) : (!StringUtils.isEmpty(dataKey) && obj.containsKey(dataKey) ? obj.getString(dataKey) : "");
        if (StringUtils.isEmpty(value)) {
            return json;
        }
        JSONObject data = this.getResultData(value);
        if (null == data) {
            return value;
        }
        if (StringUtils.isEmpty(dataKey)) {
            return data;
        }
        Object dataValue = data.containsKey(dataKey) ? data.getString(dataKey) : data;
        return dataValue;
    }

    protected String getCode(String json) {
        String code = XintBbinCode._SUCCESS.getCode();
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return code;
        }
        boolean result = obj.containsKey(RESULT_KEY) ? obj.getBoolean(RESULT_KEY) : Boolean.TRUE;
        if (result) {
            return code;
        }
        code = obj.containsKey(DATA_KEY) && null != obj.getJSONObject(DATA_KEY) && obj.getJSONObject(DATA_KEY).containsKey(ERROR_KEY) ? obj.getJSONObject(DATA_KEY).getString(ERROR_KEY) : code;
        return code;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintBbinCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            String username = this.formatLoginName(loginName);
            dto.setWebsite(WEBSITE);
            dto.setUppername(config.getMerchantCode().trim());
            dto.setUsername(username);
            dto.setKey(dto.sign(WEBSITE, username, config.getMd5key().trim()));
            respResult = this.handler(dto, config, null);
            log.info("【创建账号】平台：XINTBBIN,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBbinCode._EXCEPTION.getCode());
            log.error("【创建账号】平台：XINTBBIN,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintBbinCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            String username = this.formatLoginName(loginName);
            dto.setWebsite(WEBSITE);
            dto.setUppername(config.getMerchantCode().trim());
            dto.setUsername(username);
            dto.setKey(dto.sign(WEBSITE, username, config.getMd5key().trim()));
            respResult = this.handler(dto, config, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, XintBbinCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTBBIN,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBbinCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTBBIN, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        /**
         * 玩家额度由商户转移至平台，如果账号不存在则新建账号
         */
        ProxyConfig config = this.getProxyConfig("deposit");
        if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo) || null == amount || amount.intValue() <= 0) {
            return this.buildResult(XintBbinCode._PARAM_ERROR.getCode());
        }
        try {
            ReqTransferDTO dto = new ReqTransferDTO();
            String username = this.formatLoginName(loginName);
            dto.setWebsite(WEBSITE);
            dto.setUppername(config.getMerchantCode().trim());
            dto.setUsername(username);
            dto.setOrderNo(orderNo);
            dto.setAction(TRANSFER_IN);
            dto.setAmount(formatAmount(amount).intValue());
            dto.setKey(dto.sign(WEBSITE, username, orderNo, config.getMd5key().trim()));
            respResult = this.handler(dto, config, null, XintBbinCode._TRANSFER_SUCCESS.getCode());
            respResult.setData(formatAmount(amount).intValue());
            log.info("【转账上分】平台：XINTBBIN,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账上分】平台：XINTBBIN, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("draw");
        if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return this.buildResult(XintBbinCode._PARAM_ERROR.getCode());
        }
        try {
            ReqTransferDTO dto = new ReqTransferDTO();
            String username = this.formatLoginName(loginName);
            dto.setWebsite(WEBSITE);
            dto.setUppername(config.getMerchantCode().trim());
            dto.setUsername(username);
            dto.setOrderNo(orderNo);
            dto.setAction(TRANSFER_OUT);
            dto.setAmount(formatAmount(amount).intValue());
            dto.setKey(dto.sign(WEBSITE, username, orderNo, config.getMd5key().trim()));
            respResult = this.handler(dto, config, null, XintBbinCode._TRANSFER_SUCCESS.getCode());
            respResult.setData(formatAmount(amount).intValue());
            log.info("【转账下分】平台：XINTBBIN,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：XINTBBIN, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    private String getDateOrTime(String dateTime, String patten) {
        String date = new DateTime(dateTime, DateTimeZone.forOffsetHours(-4)).toString(patten);
        return date;
    }

    private RespResultDTO checkTransfer(RespResultDTO respResult, String loginName, Integer lang, String orderNo, BigDecimal amount, Long timestamp) {
        if (null == respResult || !respResult.getSuccess() || (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData()))) {
            return respResult;
        }
        //由于订单有同步延遲時間，同步延遲時間可能需要五秒，此处不再去校驗核对订单，直接以轉載接口返回爲準
        //RespResultDTO dto = this.searchOrder(loginName, orderNo, lang, timestamp);
        Object transferAmount = null == respResult.getData() ? formatAmount(amount).intValue() : new BigDecimal(respResult.getData().toString()).abs();
        respResult.setData(transferAmount);
        return respResult;
    }

    @Override
    public RespResultDTO searchOrder(String loginName, String orderNo, Integer lang, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("searchOrder");
            if (null == config || StringUtils.isEmpty(orderNo)) {
                return this.buildResult(XintBbinCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            String date = new DateTime(System.currentTimeMillis(), DateTimeZone.forOffsetHours(-4)).toString(UTC_PATTEN);
            String searchDate = this.getDateOrTime(date, DATE_PATTEN);
            String stTime = this.getDateOrTime(DateUtils.localToUtc(DateUtils.getFirstOfCurrentDay()), TIME_PATTEN);
            String edTime = this.getDateOrTime(DateUtils.localToUtc(DateUtils.getLastOfCurrentDay()), TIME_PATTEN);
            String username = this.formatLoginName(loginName);
            dto.setWebsite(WEBSITE);
            dto.setOrderNo(orderNo);
            dto.setUppername(config.getMerchantCode().trim());
            dto.setUsername(username);
            dto.setStartDate(searchDate);
            dto.setEndDate(searchDate);
            dto.setStartTime(stTime);
            dto.setEndTime(edTime);
            dto.setKey(dto.sign(WEBSITE, username, config.getMd5key().trim()));
            respResult = this.handlerGet(dto, config, null);
            log.info("【转账查询】平台：XINTBBIN,账号：{},结果：{}", loginName, respResult);
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || ObjectUtils.isEmpty(respResult.getData())) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintBbinCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String data = respResult.getSource();
            respResult.setData(this.getTransferAmount(data));
        } catch (Exception e) {
            respResult = this.buildResult(XintBbinCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTBBIN, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
        JSONArray arr = json.containsKey(DATA_KEY) && isJsonArry(json.getString(DATA_KEY)) ? json.getJSONArray(DATA_KEY) : null;
        if (null == arr || arr.isEmpty()) {
            return amount;
        }
        amount = null != arr.getJSONObject(0) && arr.getJSONObject(0).containsKey(ORDER_AMOUNT_KEY) ? new BigDecimal(arr.getJSONObject(0).getString(ORDER_AMOUNT_KEY)) : amount;
        return amount;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            if (null != amount && amount.intValue() > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            respResult = this.openGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            log.info("【进入游戏】平台：XINTBBIN,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBbinCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTBBIN, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    protected RespResultDTO openGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) throws Exception {
        RespResultDTO respResult = new RespResultDTO();
        ProxyConfig config = this.getProxyConfig("playGame");
        if (null == config) {
            return this.buildResult(XintBbinCode._PARAM_ERROR.getCode());
        }
        ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
        String language = LanguageCode.getLanguage(lang);
        String username = this.formatLoginName(loginName);
        dto.setWebsite(WEBSITE);
        dto.setUppername(config.getMerchantCode().trim());
        dto.setUsername(username);
        dto.setLang(language);
        dto.setPageSite(gameType);
        dto.setIngress(deviceType);
        dto.setKey(dto.sign(WEBSITE, username, config.getMd5key().trim()));
        String reqUrl = this.json2UrlParams(dto, config);
        respResult.setSuccess(Boolean.TRUE);
        respResult.setCode(String.valueOf(HttpStatus.SC_OK));
        respResult.setData(reqUrl);
        return respResult;
    }
}

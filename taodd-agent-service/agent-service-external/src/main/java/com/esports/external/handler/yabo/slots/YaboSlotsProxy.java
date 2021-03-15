package com.esports.external.handler.yabo.slots;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.yabo.slots.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.*;
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
public class
YaboSlotsProxy extends AbstractTemplate {

    private final static String TYPE = "YABODY";

    private final static String BALANCE_KEY = "balance";

    private final static String DATA_KEY = "data";

    private final static String ORDER_STATUS_KEY = "status";

    private final static String ORDER_AMOUNT_KEY = "money";

    private final static String ERROR_KEY = "code";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private final ProxyConfigManager proxyConfigManager;

    public YaboSlotsProxy() {
        proxyConfigManager = (ProxyConfigManager) SpringUtil.getBean("proxyConfigManager");
    }

    private String sign(String agent, String aesKey, Long timestamp) {
        String source = new StringBuffer(agent).append(timestamp).append(aesKey).toString();
        String md5Sign = Md5Utils.getMD5(source).toLowerCase();
        StringBuffer sb = new StringBuffer(md5Sign);
        sb.insert(9, RandomUtil.getRandomString(2));
        sb.insert(19, RandomUtil.getRandomString(2));
        sb.insert(0, RandomUtil.getRandomString(2));
        sb.append(RandomUtil.getRandomString(2));
        return sb.toString();
    }

    protected String encryption(ProxyConfig config, Long timestamp) {
        String merchantCode = config.getMerchantCode();
        String aesKey = config.getAesKey();
        RequestParamDTO model = new RequestParamDTO();
        model.setAgent(merchantCode);
        model.setRandno(RandomUtil.getRandomString(10));
        model.setSign(this.sign(merchantCode, aesKey, timestamp / 1000));
        model.setTimestamp(timestamp / 1000);
        String params = JsonUtil.object2String(model);
        return params;
    }

    protected String json2UrlParams(ProxyConfig config, Long timestamp) {
        String json = this.encryption(config, timestamp);
        SortedMap<String, Object> map = JsonUtil.string2Map(json);
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
        headers.put("Content-type", "text/plain");
        return headers;
    }

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
            return respResult;
        }
        JSONObject jsonObj = JSONObject.fromObject(json);
        String code = null != jsonObj && jsonObj.containsKey(ERROR_KEY) ? jsonObj.getString(ERROR_KEY) : "";
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = SlotsCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(SlotsCode.getMessage(code));
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
        Object dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey) ? JSONObject.fromObject(data).getString(dataKey) : "";
        return dataValue;
    }

    protected ProxyConfig getProxyConfig(String apiType) {
        String merchantType = MerchantCode._YABO.getCode();
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

    protected String createOrderId(String merchantCode, String memberId, Long timestamp) {
        StringBuffer sb = new StringBuffer(merchantCode).append("-").append(memberId).append(":").append(timestamp);
        return sb.toString();
    }

    protected RespResultDTO handler(Object dto, ProxyConfig config, Long timestamp, String dataKey, String... ignoreCodes) throws Exception {
        String data = ObjectUtils.isEmpty(dto) ? "" : JsonUtil.object2String(dto);
        String ascKey = config.getAesKey();
        String md5Key = config.getMd5key();
        String body = AESUtil.encrypt(data, ascKey, CIPHER_ALGORITHM, md5Key);
        String url = this.json2UrlParams(config, timestamp);
        String json = HttpClientUtils.post(url, body, this.getHeaders());
        RespResultDTO respResult = this.adapter(json, dataKey);
        return respResult;
    }

    protected RespResultDTO buildResult(String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(SlotsCode.getMessage(errorCode));
        return respResult;
    }

    protected String formatLoginName(ProxyConfig config, String loginName) {
        String merChantCode = config.getMerchantCode();
        StringBuffer sb = new StringBuffer(merChantCode).append("-").append(loginName);
        return sb.toString();
    }

    public String unFormatLoginName(String loginName) {
        ProxyConfig config = this.getProxyConfig("history");
        String prefix = null != config ? config.getMerchantCode() : "";
        if (!StringUtils.isEmpty(prefix)) {
            loginName = loginName.replace(prefix + "-", "");
        }
        return loginName;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            /**
             * 玩家额度由商户转移至平台，如果账号不存在则新建账号
             */
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(SlotsCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            String memberId = this.formatLoginName(config, loginName);
            dto.setMemberId(memberId);
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(SlotsCode._DEFAULT_PWD.getCode()));
            dto.setMoney(yuan2Cent(amount, new BigDecimal("100")).intValue());
            dto.setOrderId(this.createOrderId(config.getMerchantCode(), loginName, timestamp));
            dto.setDeviceType(deviceType);
            dto.setMemberIp(ip);
            respResult = this.handler(dto, config, timestamp, null);
            log.info("【转账上分】平台：YABODY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账上分】平台：YABODY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(SlotsCode._PARAM_ERROR.getCode());
            }
            //注意：商户的金额参数均以分(￥)为单位，所以取款时amount * 100
            ReqTransferDTO dto = new ReqTransferDTO();
            String memberId = this.formatLoginName(config, loginName);
            dto.setMemberId(memberId);
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(SlotsCode._DEFAULT_PWD.getCode()));
            dto.setMoney(yuan2Cent(amount, new BigDecimal("100")).intValue());
            dto.setOrderId(this.createOrderId(config.getMerchantCode(), loginName, timestamp));
            dto.setDeviceType(deviceType);
            dto.setMemberIp(ip);
            respResult = this.handler(dto, config, timestamp, null);
        } catch (Exception e) {
            log.error("【转账下分】平台：YABODY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(SlotsCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            Long timestamp = System.currentTimeMillis();
            dto.setMemberId(this.formatLoginName(config, loginName));
            dto.setMemberPwd(Md5Utils.getMD5(SlotsCode._DEFAULT_PWD.getCode()));
            dto.setMemberIp(ip);
            respResult = this.handler(dto, config, timestamp, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, SlotsCode._PARAM_ERROR.getCode());
            BigDecimal amount = new BigDecimal(respResult.getData().toString());
            respResult.setData(cent2Yuan(amount, new BigDecimal("100")));
            log.info("【获取余额】平台：YABODY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(SlotsCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：YABODY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
            if (null == config || StringUtils.isEmpty(orderNo)) {
                return this.buildResult(SlotsCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            String orderId = this.createOrderId(config.getMerchantCode().trim(), loginName, timestamp);
            dto.setOrderId(orderId);
            respResult = this.handler(dto, config, timestamp, ORDER_STATUS_KEY);
            log.info("【转账查询】平台：YABODY, 账号：{}, 结果：{}", loginName, respResult);
            String status = respResult.getData().toString();
            String errorCode = respResult.getCode();
            if (!SlotsCode.OrderStatus._SUCCESS.getCode().equals(status)) {
                errorCode = StringUtils.isEmpty(errorCode) ? SlotsCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String source = respResult.getSource();
            respResult.setData(this.getTransferAmount(source));
        } catch (Exception e) {
            respResult = this.buildResult(SlotsCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：YABODY,账号：{} , 异常信息：{}", loginName, e.getMessage());
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
        JSONObject obj = json.containsKey(DATA_KEY) ? json.getJSONObject(DATA_KEY) : null;
        if (null == obj) {
            return amount;
        }
        amount = obj.containsKey(ORDER_AMOUNT_KEY) ? new BigDecimal(obj.getString(ORDER_AMOUNT_KEY)) : amount;
        amount = cent2Yuan(amount, new BigDecimal("100"));
        return amount;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playGame");
            if (null == config) {
                return this.buildResult(SlotsCode._PARAM_ERROR.getCode());
            }
            //金额大于 0 时，视为期望同时带入余额，会校验金额和转账单号
            if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
            dto.setMemberId(this.formatLoginName(config, loginName));
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(SlotsCode._DEFAULT_PWD.getCode()));
            dto.setMemberIp(memberIp);
            dto.setGameId(gameCode);
            dto.setDeviceType(deviceType);
            respResult = this.handler(dto, config, timestamp, null);
            log.info("【进入游戏】平台：YABODY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(SlotsCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：YABODY,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }
}

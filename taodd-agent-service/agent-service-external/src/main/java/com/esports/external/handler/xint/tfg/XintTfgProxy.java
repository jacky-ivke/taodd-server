package com.esports.external.handler.xint.tfg;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.leg.XintLegCode;
import com.esports.external.handler.xint.tfg.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.SpringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
public class XintTfgProxy extends AbstractTemplate {

    private final static String TYPE = "TFG";

    private final static String DATA_KEY = "results";

    private final static String BALANCE_KEY = "balance";

    private final static String ERROR_CODE_KEY = "code";

    private final static String ORDER_STATUS_KEY = "status";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final static String SECRET = "aHR0cHM6Ly9teS5vc2NoaW5hLm5ldC91LzM2ODE4Njg";

    private final static Long EXPIRE = 86400 * 1000L;

    private final ProxyConfigManager proxyConfigManager;

    public XintTfgProxy() {
        proxyConfigManager = (ProxyConfigManager) SpringUtil.getBean("proxyConfigManager");
    }

    protected ProxyConfig getProxyConfig(String apiType) {
        String merchantType = MerchantCode._XT.getCode();
        String type = TYPE;
        ProxyConfig config = proxyConfigManager.getConfig(merchantType, type, apiType);
        return config;
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
        respResult.setMessage(XintTfgCode.getMessage(errorCode));
        return respResult;
    }

    protected RespResultDTO adapter(String json, String dataKey) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
            return respResult;
        }
        String code = this.getCode(json);
        String ok = XintTfgCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintTfgCode.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected String getCode(String json) {
        String code = "0";
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return code;
        }
        code = obj.containsKey(ERROR_CODE_KEY) ? obj.getString(ERROR_CODE_KEY) : code;
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

    protected Map<String, Object> getHeaders(ProxyConfig config) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", config.getMd5key().trim());
        return headers;
    }

    protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey) throws Exception {
        String url = config.getRquestUrl();
        Map<String, Object> params = JsonUtil.object2Map(dto);
        String json = HttpClientUtils.post(url, this.getHeaders(config), params);
        RespResultDTO respResult = this.adapter(json, dataKey);
        return respResult;
    }

    protected RespResultDTO handlerGet(Object dto, ProxyConfig config, String dataKey) throws Exception {
        String url = config.getRquestUrl();
        Map<String, Object> params = JsonUtil.object2Map(dto);
        String json = HttpClientUtils.get(url, this.getHeaders(config), params);
        RespResultDTO respResult = this.adapter(json, dataKey);
        return respResult;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintTfgCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            dto.setLoginName(loginName);
            respResult = this.handler(dto, config, null);
            log.info("【玩家注册】平台：XINTTFG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintTfgCode._EXCEPTION.getCode());
            log.error("【玩家注册】平台: XINTTFG,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config) {
                return this.buildResult(XintTfgCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            dto.setLoginName(loginName);
            respResult = this.handlerGet(dto, config, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, XintTfgCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTTFG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintTfgCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTTFG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            /**
             * 玩家额度由商户转移至平台，如果账号不存在则新建账号
             */
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo)
                    || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(XintTfgCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setLoginName(loginName);
            dto.setOperatorId(config.getMerchantCode().trim());
            dto.setOrderNo(orderNo);
            dto.setAmount(formatAmount(amount));
            respResult = this.handler(dto, config, null);
            log.info("【转账上分】平台：XINTTFG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintTfgCode._EXCEPTION.getCode());
            log.error("【转账上分】平台：XINTTFG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintTfgCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setLoginName(loginName);
            dto.setOperatorId(config.getMerchantCode().trim());
            dto.setOrderNo(orderNo);
            dto.setAmount(formatAmount(amount));
            respResult = this.handler(dto, config, null);
            log.info("【转账下分】平台：XINTTFG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：XINTTFG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playGame");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintTfgCode._PARAM_ERROR.getCode());
            }
            //金额大于 0 时，视为期望同时带入余额，会校验金额和转账单号
            if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            respResult = this.openGame(config, loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo, timestamp);
            log.info("【进入游戏】平台：XINTTFG, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintTfgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTIA,账号：{} , 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintTfgCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setOrderNo(orderNo);
            respResult = this.handlerGet(dto, config, ORDER_STATUS_KEY);
            String status = respResult.getData().toString();
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || !XintTfgCode.OrderStatus._SUCCESS.getCode().equals(status)) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintLegCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String source = respResult.getSource();
            respResult.setData(this.getTransferAmount(source));
            log.info("【转账查询】平台：XINTFTG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintTfgCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTFTG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
        amount = arr.getJSONObject(0).containsKey(ORDER_AMOUNT_KEY) ? new BigDecimal(arr.getJSONObject(0).getString(ORDER_AMOUNT_KEY)) : amount;
        return amount;
    }

    private String createToken(String loginName) {
        String token = "";
        try {
            Date expireDate = new Date(System.currentTimeMillis() + EXPIRE);
            token = Jwts.builder().setHeaderParam("auth", "JWT") // 设置头部信息
                    .setSubject(loginName).setExpiration(expireDate) // token过期时间
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
            log.info("【登入认证】平台：XINTTFG,TOKEN：{}", token);
        } catch (Exception e) {
            log.error("【登入认证】平台:XINTTFG, 异常信息:{}", e.getMessage());
        }
        return token;
    }

    private static Claims getClaimByToken(String token) {
        Claims claims = null;
        if (!StringUtils.isEmpty(token)) {
            try {
                // 得到DefaultJwtParser
                claims = Jwts.parser()
                        // 设置签名的秘钥
                        .setSigningKey(SECRET)
                        // 设置需要解析的jwt
                        .parseClaimsJws(token).getBody();
            } catch (Exception e) {
                claims = null;
            }
        }
        return claims;
    }

    public static String getSubject(String token) {
        String result = "";
        Claims claims = getClaimByToken(token);
        if (null != claims) {
            result = claims.getSubject();
        }
        return result;
    }

    protected RespResultDTO openGame(ProxyConfig config, String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = new RespResultDTO();
        String userToken = this.createToken(loginName);
        ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
        dto.setToken(userToken);
        dto.setAuth(config.getAesKey().trim());
        String reqUrl = this.json2UrlParams(dto, config);
        respResult.setSuccess(Boolean.TRUE);
        respResult.setCode(String.valueOf(HttpStatus.SC_OK));
        respResult.setData(reqUrl);
        return respResult;
    }

}

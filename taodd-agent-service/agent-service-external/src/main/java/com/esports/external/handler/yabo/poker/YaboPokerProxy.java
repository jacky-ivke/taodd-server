package com.esports.external.handler.yabo.poker;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.yabo.poker.dto.*;
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
public class YaboPokerProxy extends AbstractTemplate {

    private final static String TYPE = "YABOQP";

    private final static String BALANCE_KEY = "balance";

    private final static String DATA_KEY = "data";

    private final static String ORDER_STATUS_KEY = "status";

    private final static String ORDER_AMOUNT_KEY = "money";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private final ProxyConfigManager proxyConfigManager;

    public YaboPokerProxy() {
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
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        RespResultDTO respResult = JsonUtil.string2Object(json, RespResultDTO.class);
        String code = respResult.getCode();
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = PokerCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(PokerCode.getMessage(code));
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

    protected RespResultDTO handlder(Object dto, ProxyConfig config, Long timestamp, String dataKey, String... ignoreCodes) throws Exception {
        String data = JsonUtil.object2String(dto);
        String ascKey = config.getAesKey();
        String md5Key = config.getMd5key();
        String body = AESUtil.encrypt(data, ascKey, CIPHER_ALGORITHM, md5Key);
        String url = this.json2UrlParams(config, timestamp);
        String json = HttpClientUtils.post(url, body, this.getHeaders());
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO buildResult(String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(PokerCode.getMessage(errorCode));
        return respResult;
    }

    protected String formatLoginName(ProxyConfig config, String loginName) {
        String merchantCode = config.getMerchantCode();
        StringBuffer sb = new StringBuffer(merchantCode).append("-").append(loginName);
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
             * 玩家额度由商户转移至平台,如果账号不存在则新建账号
             */
            ProxyConfig config = this.getProxyConfig("deposit");
            if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return this.buildResult(PokerCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            String memberId = this.formatLoginName(config, loginName);
            String orderId = this.createOrderId(config.getMerchantCode().trim(), loginName, timestamp);
            dto.setMemberId(memberId);
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(PokerCode._DEFAULT_PWD.getCode()));
            dto.setMoney(yuan2Cent(amount, new BigDecimal("100")).intValue());
            dto.setOrderId(orderId);
            dto.setDeviceType(deviceType);
            dto.setMemberIp(ip);
            respResult = this.handlder(dto, config, timestamp, null);
            log.info("【转账上分】平台：YABOQP, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账上分】平台：YABOQP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(PokerCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            String memberId = this.formatLoginName(config, loginName);
            String orderId = this.createOrderId(config.getMerchantCode().trim(), loginName, timestamp);
            dto.setMemberId(memberId);
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(PokerCode._DEFAULT_PWD.getCode()));
            dto.setMoney(yuan2Cent(amount, new BigDecimal("100")).intValue());
            dto.setOrderId(orderId);
            dto.setDeviceType(deviceType);
            dto.setMemberIp(ip);
            respResult = this.handlder(dto, config, timestamp, null);
        } catch (Exception e) {
            log.error("【转账下分】平台：YABOQP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(PokerCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            Long timestamp = System.currentTimeMillis();
            dto.setMemberId(this.formatLoginName(config, loginName));
            dto.setMemberPwd(Md5Utils.getMD5(PokerCode._DEFAULT_PWD.getCode()));
            dto.setMemberIp(ip);
            respResult = this.handlder(dto, config, timestamp, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, PokerCode._PARAM_ERROR.getCode());
            BigDecimal amount = new BigDecimal(respResult.getData().toString());
            respResult.setData(cent2Yuan(amount, new BigDecimal("100")));
            log.info("【获取余额】平台：YABOQP, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(PokerCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：YABOQP, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        if (StringUtils.isEmpty(gameCode)) {
            return this.playGame(loginName, deviceType, memberIp, lang, amount, transferNo, timestamp);
        }
        respResult = this.playGmeById(loginName, deviceType, memberIp, lang, gameCode, amount, transferNo, timestamp);
        return respResult;
    }

    protected RespResultDTO playGmeById(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            /**
             * 给玩家提供登录某一个游戏的服务,如果账号不存在则新建账号,玩家登录成功返回登录单个 游戏 URL 地址
             */
            ProxyConfig config = this.getProxyConfig("playOneGame");
            if (null == config) {
                return this.buildResult(PokerCode._PARAM_ERROR.getCode());
            }
            //金额大于 0 时，视为期望同时带入余额，会校验金额和转账单号
            if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
            dto.setMemberId(this.formatLoginName(config, loginName));
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(PokerCode._DEFAULT_PWD.getCode()));
            dto.setMemberIp(memberIp);
            dto.setGameId(gameCode);
            dto.setDeviceType(deviceType);
            respResult = this.handlder(dto, config, timestamp, null);
            log.info("【进入游戏】平台：YABOQP, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(PokerCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：YABOQP,账号：{} , 异常信息：{}", loginName, e.getMessage());
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
            /**
             * 来查询平台玩家上下分的订单信息,通过 status 状态来判断上下分是否成功
             */
            ProxyConfig config = this.getProxyConfig("searchOrder");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(PokerCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            String orderId = this.createOrderId(config.getMerchantCode().trim(), loginName, timestamp);
            dto.setOrderId(orderId);
            respResult = this.handlder(dto, config, timestamp, ORDER_STATUS_KEY);
            log.info("【查询订单】平台：YABOQP, 账号：{}, 结果：{}", loginName, respResult);
            String status = respResult.getData().toString();
            String errorCode = respResult.getCode();
            if (!PokerCode.OrderStatus._SUCCESS.getCode().equals(status)) {
                errorCode = StringUtils.isEmpty(errorCode) ? PokerCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String source = respResult.getSource();
            respResult.setData(this.getTransferAmount(source));
        } catch (Exception e) {
            respResult = this.buildResult(PokerCode._EXCEPTION.getCode());
            log.error("【查询订单】平台：YABOQP,账号：{} , 异常信息：{}", loginName, e.getMessage());
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


    protected RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            /**
             * 给玩家提供登录服务,如果账号不存在则新建账号,玩家登录成功返回登录游戏 URL地址。
             */
            ProxyConfig config = this.getProxyConfig("playGame");
            if (null == config) {
                return this.buildResult(PokerCode._PARAM_ERROR.getCode());
            }
            if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
                this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
            }
            ReqCreateLoginPlayerDTO dto = new ReqCreateLoginPlayerDTO();
            dto.setMemberId(this.formatLoginName(config, loginName));
            dto.setMemberName(loginName);
            dto.setMemberPwd(Md5Utils.getMD5(PokerCode._DEFAULT_PWD.getCode()));
            dto.setMemberIp(memberIp);
            dto.setDeviceType(deviceType);
            respResult = this.handlder(dto, config, timestamp, null);
            log.info("【进入游戏】平台：YABOQP, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(PokerCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：YABOQP,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }
}

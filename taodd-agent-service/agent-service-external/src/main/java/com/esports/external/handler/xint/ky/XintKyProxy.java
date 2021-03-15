package com.esports.external.handler.xint.ky;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.ky.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.AESUtil;
import com.esports.utils.JsonUtil;
import com.esports.utils.Md5Utils;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class XintKyProxy extends AbstractTemplate {

    private final static String TYPE = "KY";

    private final static String BALANCE_KEY = "money";

    private final static String GAME_URL_KEY = "url";

    private final static String ERROR_KEY = "code";

    private final static String DATA_KEY = "d";

    private final static String ORDER_STATUS_KEY = "status";

    private final static String ORDER_AMOUNT_KEY = "money";

    private final static String CHARSET = "utf-8";

    private final ProxyConfigManager proxyConfigManager;

    public XintKyProxy() {
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

    protected String createOrderId(String merchantCode, String account, Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String orderNo = sdf.format(new Date(timestamp));
        StringBuffer sb = new StringBuffer(merchantCode).append(orderNo).append(account);
        return sb.toString();
    }

    protected ProxyConfig getProxyConfig(String apiType) {
        String merchantType = MerchantCode._XT.getCode();
        String type = TYPE;
        ProxyConfig config = proxyConfigManager.getConfig(merchantType, type, apiType);
        return config;
    }

    private String sign(String agent, String md5Key, Long timestamp) {
        String source = new StringBuffer(agent).append(timestamp).append(md5Key).toString();
        String md5Sign = Md5Utils.getMD5(source).toLowerCase();
        return md5Sign;
    }

    protected String encryption(ProxyConfig config, Object dto, Long timestamp) throws Exception {
        String merchantCode = config.getMerchantCode();
        String md5Key = config.getMd5key();
        String ascKey = config.getAesKey();
        String source = JsonUtil.object2String(dto);
        String param = this.urlParams(source);
        RequestParamDTO model = new RequestParamDTO();
        model.setAgent(merchantCode);
        model.setParam(URLEncoder.encode(AESUtil.encrypt(param, ascKey), CHARSET));
        model.setKey(this.sign(merchantCode, md5Key, timestamp));
        model.setTimestamp(timestamp);
        String params = JsonUtil.object2String(model);
        return params;
    }

    private String urlParams(String json) {
        String params = json.replace("{", "").replace("}", "").replace(":", "=").replace(",", "&").replace("\"", "");
        return params;
    }

    protected String json2UrlParams(Object dto, ProxyConfig config, Long timestamp) throws Exception {
        String json = this.encryption(config, dto, timestamp);
        String url = config.getRquestUrl();
        String urlParams = "";
        if (StringUtils.isEmpty(json)) {
            return urlParams;
        }
        urlParams = this.urlParams(json);
        String reqUrl = new StringBuffer(url).append(urlParams).toString();
        return reqUrl;
    }

    protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
        RespResultDTO respResult = new RespResultDTO();
        if (StringUtils.isEmpty(json) || !this.isJsonObject(json)) {
            return respResult;
        }
        JSONObject jsonObj = JSONObject.fromObject(json);
        String code = null != jsonObj && jsonObj.containsKey(DATA_KEY)
                && null != jsonObj.getJSONObject(DATA_KEY) && jsonObj.getJSONObject(DATA_KEY).containsKey(ERROR_KEY) ? jsonObj.getJSONObject(DATA_KEY).getString(ERROR_KEY) : null;
        boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
        String ok = XintKyCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintKyCode.getMessage(code));
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

    protected RespResultDTO handler(Object dto, ProxyConfig config, Long timestamp, String dataKey, String... ignoreCodes) throws Exception {
        String url = this.json2UrlParams(dto, config, timestamp);
        String json = HttpClientUtils.get(url);
        RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
        return respResult;
    }

    protected RespResultDTO buildResult(String errorCode) {
        RespResultDTO respResult = new RespResultDTO();
        respResult.setSuccess(Boolean.FALSE);
        respResult.setCode(errorCode);
        respResult.setMessage(XintKyCode.getMessage(errorCode));
        return respResult;
    }

    public static String unFormatLoginName(String loginName) {
        loginName = loginName.substring(loginName.indexOf("_") + 1);
        return loginName;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintKyCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            Long timestamp = System.currentTimeMillis();
            dto.setS("1");
            dto.setAccount(loginName);
            respResult = this.handler(dto, config, timestamp, BALANCE_KEY);
            respResult = this.buildBalanceResult(respResult, XintKyCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTKY,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintKyCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTKY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintKyCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setAccount(loginName);
            dto.setS("2");
            dto.setOrderid(this.createOrderId(config.getMerchantCode(), loginName, timestamp));
            dto.setMoney(formatAmount(amount));
            respResult = this.handler(dto, config, timestamp, null);
            log.info("【转账上分】平台：XINTKY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账上分】平台：XINTKY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintKyCode._PARAM_ERROR.getCode());
            }
            ReqTransferDTO dto = new ReqTransferDTO();
            dto.setAccount(loginName);
            dto.setS("3");
            dto.setOrderid(this.createOrderId(config.getMerchantCode(), loginName, timestamp));
            dto.setMoney(formatAmount(amount));
            respResult = this.handler(dto, config, timestamp, null);
            log.info("【转账下分】平台：XINTKY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            log.error("【转账下分】平台：XINTKY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
            ProxyConfig config = this.getProxyConfig("draw");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintKyCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            dto.setS("4");
            dto.setOrderid(this.createOrderId(config.getMerchantCode(), loginName, timestamp));
            respResult = this.handler(dto, config, timestamp, ORDER_STATUS_KEY);
            String status = respResult.getData().toString();
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || !XintKyCode.OrderStatus._SUCCESS.getCode().equals(status)) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintKyCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            String source = respResult.getSource();
            respResult.setData(this.getTransferAmount(source));
            log.info("【转账查询】平台：XINTKY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintKyCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTKY, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
        return amount;
    }

    @Override
    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playGame");
            if (null == config) {
                return this.buildResult(XintKyCode._PARAM_ERROR.getCode());
            }
            ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
            dto.setS("0");
            dto.setAccount(loginName);
            dto.setMoney(amount);
            dto.setOrderid(this.createOrderId(config.getMerchantCode(), loginName, timestamp));
            dto.setIp(memberIp);
            dto.setLineCode(config.getMerchantCode());
            dto.setKindID(StringUtils.isEmpty(gameCode) ? "0" : gameCode);
            respResult = this.handler(dto, config, timestamp, GAME_URL_KEY);
            log.info("【进入游戏】平台：XINTKY, 账号：{}, 结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintKyCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTKY,账号：{} , 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }
}

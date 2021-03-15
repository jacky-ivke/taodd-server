package com.esports.external.handler.xint.bg;


import com.esports.constant.GameTypeCode;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.bg.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
import com.esports.utils.Md5Utils;
import com.esports.utils.ShaUtil;
import com.esports.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class XintBgProxy extends AbstractTemplate {

    private final static String TYPE = "BG";

    private final static String ERROR_KEY = "error";

    private final static String ERROR_CODE = "code";

    private final static String DATA_KEY = "result";

    private final static String ORDER_LIST_KEY = "items";

    private final static String ORDER_AMOUNT_KEY = "amount";

    private final static String SN = "am00";

    private final ProxyConfigManager proxyConfigManager;

    public XintBgProxy() {
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
        respResult.setMessage(XintBgCode.getMessage(errorCode));
        return respResult;
    }

    protected String sign(String encrypted) {
        String sign = Md5Utils.getMD5(encrypted).toLowerCase();
        return sign;
    }

    protected String getSecretCode(ProxyConfig config) {
        String password = config.getMd5key();
        String secretCode = ShaUtil.sha1Base64(password);
        return secretCode;
    }

    protected RespResultDTO handler(String method, Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
        String params = JsonUtil.object2String(dto);
        RequestParamDTO model = new RequestParamDTO();
        model.setMethod(method);
        model.setParams(JSONObject.fromObject(params));
        String body = JsonUtil.object2String(model);
        String url = config.getRquestUrl();
        String json = HttpClientUtils.post(url, body, null);
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
        String ok = XintBgCode._SUCCESS.getCode();
        respResult.setSuccess(ok.equals(code) || ignore ? Boolean.TRUE : Boolean.FALSE);
        respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
        respResult.setData(this.getData(json, dataKey));
        respResult.setMessage(XintBgCode.getMessage(code));
        respResult.setSource(json);
        return respResult;
    }

    protected String getCode(String json) {
        String code = XintBgCode._SUCCESS.getCode();
        JSONObject obj = JSONObject.fromObject(json);
        String result = obj.containsKey(ERROR_KEY) && !StringUtils.isEmpty(obj.getString(ERROR_KEY)) ? obj.getString(ERROR_KEY) : "";
        if (StringUtils.isEmpty(result)) {
            return code;
        }
        JSONObject errorJson = JSONObject.fromObject(result);
        code = null != errorJson && errorJson.containsKey(ERROR_CODE) ? errorJson.getString(ERROR_CODE) : code;
        return code;
    }

    protected Object getData(String json, String dataKey) {
        JSONObject obj = JSONObject.fromObject(json);
        if (null == obj) {
            return null;
        }
        String data = obj.containsKey(DATA_KEY) ? obj.getString(DATA_KEY) : "";
        if (StringUtils.isEmpty(data)) {
            return json;
        }
        if (StringUtils.isEmpty(dataKey)) {
            return data;
        }
        Object dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey) ? JSONObject.fromObject(data).getString(dataKey) : data;
        return dataValue;
    }

    protected RespResultDTO create(String loginName, Integer lang) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("create");
            if (null == config || StringUtils.isEmpty(loginName)) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(this.getSecretCode(config));
            dto.setSn(SN);
            dto.setRandom(uuid);
            dto.setDigest(this.sign(digest.toString()));
            dto.setLoginId(loginName);
            dto.setAgentLoginId(config.getMerchantCode());
            dto.setNickname(loginName);
            respResult = this.handler(dto.getMethod(), dto, config, null, XintBgCode._ACCOUNT_EXISTS.getCode());
            log.info("【创建账号】平台：XINTBG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【创建账号】平台:XINTBG,账号：{}, 异常信息:{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO balance(String loginName, String ip) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("balance");
            if (null == config) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(loginName).append(this.getSecretCode(config));
            dto.setSn(SN);
            dto.setLoginId(loginName);
            dto.setRandom(uuid);
            dto.setDigest(this.sign(digest.toString()));
            respResult = this.handler(dto.getMethod(), dto, config, null);
            respResult = this.buildBalanceResult(respResult, XintBgCode._PARAM_ERROR.getCode());
            log.info("【获取余额】平台：XINTBG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【获取余额】平台：XINTBG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    @Override
    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("deposit");
        if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
        }
        String money = formatAmount(amount).toString();
        try {
            ReqTransferDTO dto = new ReqTransferDTO();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(loginName).append(money).append(this.getSecretCode(config));
            dto.setSn(SN);
            dto.setLoginId(loginName);
            dto.setRandom(uuid);
            dto.setDigest(this.sign(digest.toString()));
            dto.setBizId(orderNo);
            dto.setAmount(money);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【转账上分】平台：XINTBG,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintBgCode._ID_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
                return respResult;
            }
        } catch (Exception e) {
            log.error("【转账上分】平台：XINTBG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        } finally {
            respResult = this.checkTransfer(respResult, loginName, lang, orderNo, formatAmount(amount), timestamp);
        }
        return respResult;
    }

    @Override
    public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
        RespResultDTO respResult = null;
        ProxyConfig config = this.getProxyConfig("draw");
        if (null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
        }
        String money = formatAmount(amount).negate().toString();
        try {
            ReqTransferDTO dto = new ReqTransferDTO();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(loginName).append(money).append(this.getSecretCode(config));
            dto.setSn(SN);
            dto.setLoginId(loginName);
            dto.setRandom(uuid);
            dto.setDigest(this.sign(digest.toString()));
            dto.setBizId(orderNo);
            dto.setAmount(money);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【转账下分】平台：XINTBG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【转账下分】平台：XINTBG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqGetTransferDTO dto = new ReqGetTransferDTO();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(config.getAesKey());
            dto.setSn(SN);
            dto.setLoginId(loginName);
            dto.setRandom(uuid);
            dto.setSign(this.sign(digest.toString()));
            dto.setBizId(orderNo);
            dto.setAgentLoginId(config.getMerchantCode());
            respResult = this.handler(dto.getMethod(), dto, config, ORDER_LIST_KEY);
            String data = null != respResult.getData() ? respResult.getData().toString() : "";
            String errorCode = respResult.getCode();
            if (!respResult.getSuccess() || StringUtils.isEmpty(data) || !isJsonArry(data) || JSONArray.fromObject(data).size() < 1) {
                errorCode = StringUtils.isEmpty(errorCode) ? XintBgCode._ORDER_FAILURE.getCode() : errorCode;
                respResult = this.buildResult(errorCode);
            }
            respResult.setData(this.getTransferAmount(data));
            log.info("【转账查询】平台：XINTBG,账号：{},结果：{}", loginName, respResult);
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【转账查询】平台：XINTBG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    private BigDecimal getTransferAmount(String data) {
        BigDecimal amount = BigDecimal.ZERO;
        JSONArray arr = JSONArray.fromObject(data);
        if (null == arr || arr.size() <= 0) {
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
        //金额大于 0 时，视为期望同时带入余额，会校验金额和转账单号
        if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
        }
        RespResultDTO respResult = null;
        if (GameTypeCode._LIVE.getCode().equalsIgnoreCase(gameType)) {
            respResult = this.playLiveGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
        } else if (GameTypeCode._SLOTS.getCode().equalsIgnoreCase(gameType)) {
            respResult = this.playSlotsGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
        } else if (GameTypeCode._SPORTS.getCode().equalsIgnoreCase(gameType)) {

        } else if (GameTypeCode._FISH_ARCADE.getCode().equalsIgnoreCase(gameType)) {
            respResult = this.playFishGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
        } else if (GameTypeCode._TABLE_GAME.getCode().equalsIgnoreCase(gameType)) {
            respResult = this.playPokerGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
        } else if (GameTypeCode._LOTTO.getCode().equalsIgnoreCase(gameType)) {
            respResult = this.playLotteryGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
        }
        return respResult;
    }

    public RespResultDTO playLiveGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playLiveGame");
            if (null == config) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqLiveCreateDepositLoginPlayerDTO dto = new ReqLiveCreateDepositLoginPlayerDTO();
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(loginName).append(this.getSecretCode(config));
            dto.setSn(SN);
            dto.setRandom(uuid);
            dto.setDigest(this.sign(digest.toString()));
            dto.setLocale(language);
            dto.setLoginId(loginName);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【进入游戏】平台：XINTBG >> 真人视讯 ,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintBgCode._ID_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            //玩家登录游戏，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.playLiveGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
                return respResult;
            }
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTBG >> 真人视讯 , 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    public RespResultDTO playFishGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playFishGame");
            if (null == config) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqFishCreateDepositLoginPlayerDTO dto = new ReqFishCreateDepositLoginPlayerDTO();
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(config.getAesKey().trim());
            dto.setSn(SN);
            dto.setRandom(uuid);
            dto.setSign(this.sign(digest.toString()));
            dto.setLang(language);
            dto.setLoginId(loginName);
            dto.setGameType(gameCode);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【进入游戏】平台：XINTBG >> 捕鱼游戏 ,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintBgCode._ID_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            //玩家登录游戏，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.playFishGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
                return respResult;
            }
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTBG >> 捕鱼游戏 , 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    public RespResultDTO playPokerGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playPokerGame");
            if (null == config) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqPokerCreateDepositLoginPlayerDTO dto = new ReqPokerCreateDepositLoginPlayerDTO();
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(config.getAesKey().trim());
            dto.setSn(SN);
            dto.setRandom(uuid);
            dto.setSign(this.sign(digest.toString()));
            dto.setLang(language);
            dto.setLoginId(loginName);
            dto.setGameId(gameCode);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【进入游戏】平台：XINTBG >> 捕鱼游戏 ,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintBgCode._ID_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            //玩家登录游戏，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.playPokerGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
                return respResult;
            }
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTBG >> 捕鱼游戏 , 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    public RespResultDTO playLotteryGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playLotteryGame");
            if (null == config) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqLotteryCreateDepositLoginPlayerDTO dto = new ReqLotteryCreateDepositLoginPlayerDTO();
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(loginName).append(this.getSecretCode(config));
            dto.setSn(SN);
            dto.setRandom(uuid);
            dto.setDigest(this.sign(digest.toString()));
            dto.setLocal(language);
            dto.setLoginId(loginName);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【进入游戏】平台：XINTBG >> 彩票游戏 ,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintBgCode._ID_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            //玩家登录游戏，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.playLotteryGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
                return respResult;
            }

        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTBG >> 彩票游戏 , 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }

    public RespResultDTO playSlotsGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo) {
        RespResultDTO respResult = null;
        try {
            ProxyConfig config = this.getProxyConfig("playSlotsGame");
            if (null == config) {
                return this.buildResult(XintBgCode._PARAM_ERROR.getCode());
            }
            ReqSlotsCreateDepositLoginPlayerDTO dto = new ReqSlotsCreateDepositLoginPlayerDTO();
            String language = LanguageCode.getLanguage(lang);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            StringBuffer digest = new StringBuffer(uuid).append(SN).append(config.getAesKey().trim());
            dto.setSn(SN);
            dto.setRandom(uuid);
            dto.setSign(this.sign(digest.toString()));
            dto.setLocal(language);
            dto.setLoginId(loginName);
            respResult = this.handler(dto.getMethod(), dto, config, null);
            log.info("【进入游戏】平台：XINTBG >> 电子游戏 ,账号：{},结果：{}", loginName, respResult);
            if (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
                return respResult;
            }
            boolean success = this.check(respResult, XintBgCode._ID_NOT_EXISTS.getCode());
            if (!success) {
                return respResult;
            }
            //玩家登录游戏，如果账号不存在则新建账号
            respResult = this.create(loginName, lang);
            success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
            if (success) {
                respResult = this.playLotteryGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo);
                return respResult;
            }
        } catch (Exception e) {
            respResult = this.buildResult(XintBgCode._EXCEPTION.getCode());
            log.error("【进入游戏】平台：XINTBG >> 电子游戏 , 账号：{}, 异常信息：{}", loginName, e.getMessage());
        }
        return respResult;
    }
}

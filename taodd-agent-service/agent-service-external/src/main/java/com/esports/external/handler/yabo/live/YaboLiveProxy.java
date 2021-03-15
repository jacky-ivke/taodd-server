package com.esports.external.handler.yabo.live;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.yabo.live.dto.*;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class YaboLiveProxy extends AbstractTemplate {

	private final static String TYPE = "YABOZR";
	
	private final static String GAME_URL_KEY = "url";
	
	private final static String BALANCE_KEY = "balance";
	
	private final static String ORDER_STATUS_KEY = "transferStatus";
	
	private final static String ORDER_AMOUNT_KEY = "amount";
	
	private final static String DATA_KEY = "data";
	
	private final ProxyConfigManager proxyConfigManager;
	
	public YaboLiveProxy() {
		proxyConfigManager = (ProxyConfigManager) SpringUtil.getBean("proxyConfigManager");
	}

	protected boolean check(RespResultDTO result) {
		boolean success = false;
		if (ObjectUtils.isEmpty(result)) {
			return success;
		}
		String ok = LiveCode._SUCCESS.getCode();
		if (ok.equals(result.getCode())) {
			success = true;
		}
		return success;
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

	protected String encryption(Object object, ProxyConfig config) {
		String merchantCode = config.getMerchantCode();
		String md5Key = config.getMd5key();
		String aesKey=  config.getAesKey();
		String source = JsonUtil.object2String(object);
		RequestParamDTO model = new RequestParamDTO();
		model.setMerchantCode(merchantCode);
		model.setParams(AESUtil.encrypt(source, aesKey));
		model.setSignature(Md5Utils.getMD5(source + md5Key));
		String params = JsonUtil.object2String(model);
		return params;
	}

	protected ProxyConfig getProxyConfig(String apiType) {
		String merchantType = MerchantCode._YABO.getCode();
		String type = TYPE;
		ProxyConfig config = proxyConfigManager.getConfig(merchantType, type, apiType);
		return config;
	}

	protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
		RespResultDTO respResult = new RespResultDTO();
		if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
			return respResult;
		}
		String ok = LiveCode._SUCCESS.getCode();	
		respResult = JsonUtil.string2Object(json, RespResultDTO.class);
		String code = respResult.getCode();
		boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
		respResult.setSuccess(ok.equals(code) || ignore? Boolean.TRUE : Boolean.FALSE);
		respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
		respResult.setData(this.getData(json, dataKey));
		respResult.setMessage(LiveCode.getMessage(code));
		respResult.setSource(json);
		return respResult;
	}
	
	protected Object getData(String json, String dataKey) {
		JSONObject obj = JSONObject.fromObject(json);
		if(null == obj) {
			return null;
		}
		String data = obj.containsKey(DATA_KEY)? obj.getString(DATA_KEY) : "";
		if(StringUtils.isEmpty(data)) {
			return json;
		}
		if(StringUtils.isEmpty(dataKey)) {
			return data;
		}
		Object dataValue = isJsonObject(data) && JSONObject.fromObject(data).containsKey(dataKey)? JSONObject.fromObject(data).getString(dataKey) : "";
		return dataValue;
	}
	
	protected RespResultDTO buildResult(String errorCode) {
		RespResultDTO respResult = new RespResultDTO();
		respResult.setSuccess(Boolean.FALSE);
		respResult.setCode(errorCode);
		respResult.setMessage(LiveCode.getMessage(errorCode));
		return respResult;
	}
	
	protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
		String params = this.encryption(dto, config);
		String url = config.getRquestUrl();
		String json = HttpClientUtils.post(url, params, null);
		RespResultDTO respResult = this.adapter(json, dataKey);
		return respResult;
	}
	
	protected Map<String, Object> getHeaders(String merchantCode, Long pageIndex){
		Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("merchantCode", merchantCode);
        headers.put("pageIndex", pageIndex);
		return headers;
	}
	
	protected String formatLoginName(ProxyConfig config, String loginName) {
		String merchantCode = config.getMerchantCode();
		StringBuffer sb = new StringBuffer(merchantCode).append(loginName);
		return sb.toString();
	}
	
	protected RespResultDTO create(String loginName, Integer lang) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("create");
			if (null == config || StringUtils.isEmpty(loginName)) {
				return this.buildResult(LiveCode._MISSING_PARAM.getCode());
			}
			ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
			String language = LanguageCode.getLanguage(lang);
			dto.setLoginName(this.formatLoginName(config, loginName));
			dto.setLoginPassword(LiveCode._DEFAULT_PWD.getCode());
			dto.setNickName(loginName);
			dto.setLang(Integer.parseInt(language));
			dto.setTimestamp(System.currentTimeMillis());
			respResult = this.handler(dto, config, null, LiveCode._ACCOUNT_EXISTS.getCode());
			log.info("【创建账号】平台：YABOZR,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(LiveCode._EXCEPTION.getCode());
			log.error("【创建账号】平台YABOZR,账号：{}, 异常信息:{}", loginName, e.getMessage());
		}
		return respResult;
	}

	@Override
	public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("deposit");
			if (null == config || StringUtils.isEmpty(loginName) || StringUtils.isEmpty(orderNo) 
					|| null == amount || amount.compareTo(BigDecimal.ZERO)<=0) {
				return this.buildResult(LiveCode._MISSING_PARAM.getCode());
			}
			ReqTransferDTO dto = new ReqTransferDTO();
			amount = formatAmount(amount);
			dto.setLoginName(this.formatLoginName(config, loginName));
			dto.setAmount(amount);
			dto.setTransferNo(orderNo);
			dto.setTimestamp(timestamp);
			respResult = this.handler(dto, config, null);
			if(StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
				return respResult;
			}
			boolean success = this.check(respResult, LiveCode._ACCOUNT_NOT_EXISTS.getCode());
			if (!success) {
				return respResult;
			}
			// 玩家额度由商户转移至平台，如果账号不存在则新建账号
			respResult = this.create(loginName, lang);
			success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if (success) {
				respResult = deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
				return respResult;
			}
			log.info("【转账上分】平台：YABOZR,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			log.error("【转账上分】平台：YABOZR, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
					|| null == amount || amount.compareTo(BigDecimal.ZERO)<=0) {
				return this.buildResult(LiveCode._MISSING_PARAM.getCode());
			}
			ReqTransferDTO dto = new ReqTransferDTO();
			amount = formatAmount(amount);
			dto.setLoginName(this.formatLoginName(config, loginName));
			dto.setAmount(amount.setScale(2, BigDecimal.ROUND_DOWN));
			dto.setTransferNo(orderNo);
			dto.setTimestamp(timestamp);
			respResult = this.handler(dto, config, null);
			log.info("【转账下分】平台：YABOZR,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			log.error("【转账下分】平台：YABOZR, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
			if (null == config || StringUtils.isEmpty(loginName) ) {
				return this.buildResult(LiveCode._MISSING_PARAM.getCode());
			}
			ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
			dto.setLoginName(this.formatLoginName(config, loginName));
			dto.setTimestamp(System.currentTimeMillis());
			respResult = this.handler(dto, config, BALANCE_KEY);
			respResult = this.buildBalanceResult(respResult, LiveCode._MISSING_PARAM.getCode());
			log.info("【获取余额】平台：YABOZR,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(LiveCode._EXCEPTION.getCode());
			log.error("【获取余额】平台：YABOZR, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}
	
	private RespResultDTO checkTransfer(RespResultDTO respResult, String loginName, Integer lang, String orderNo, BigDecimal amount, Long timestamp) {
		if(null == respResult || !respResult.getSuccess() || (StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData()))) {
			return respResult;
		}
		RespResultDTO dto = this.searchOrder(loginName, orderNo, lang, timestamp);
		BigDecimal transferAmount = null == dto.getData()? amount : new BigDecimal(dto.getData().toString()).abs();
		dto.setData(transferAmount);
		return dto;
	}
	
	@Override
	public RespResultDTO searchOrder(String loginName, String orderNo, Integer lang, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("searchOrder");
			if (null == config || StringUtils.isEmpty(orderNo)) {
				return this.buildResult(LiveCode._MISSING_PARAM.getCode());
			}
			ReqGetTransferDTO dto = new ReqGetTransferDTO();
			dto.setTransferNo(orderNo);
			dto.setLoginName(this.formatLoginName(config, loginName));	
			dto.setTimestamp(timestamp);
			respResult = this.handler(dto, config, ORDER_STATUS_KEY);
			String status = respResult.getData().toString();
			String errorCode = respResult.getCode();
			if(!LiveCode.OrderStatus.SUCCESS.getCode().equals(status)) {
				errorCode = StringUtils.isEmpty(errorCode)? LiveCode._ORDER_FAILURE.getCode() : errorCode;
				respResult = this.buildResult(errorCode);
			}
			String source = respResult.getSource();
			respResult.setData(this.getTransferAmount(source));
			log.info("【转账查询】平台：YABOZR,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(LiveCode._EXCEPTION.getCode());
			log.error("【转账查询】平台：YABOZR, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}
	
	private BigDecimal getTransferAmount(String data) {
		BigDecimal amount = BigDecimal.ZERO;
		if(!isJsonObject(data)) {
			return amount;
		}
		JSONObject json = JSONObject.fromObject(data);
		if(null == json) {
			return amount;
		}
		JSONObject obj = json.containsKey(DATA_KEY)? json.getJSONObject(DATA_KEY):null;
		if(null == obj) {
			return amount;
		}
		amount = obj.containsKey(ORDER_AMOUNT_KEY)? new BigDecimal(obj.getString(ORDER_AMOUNT_KEY)) : amount;
		return amount;
	}

	@Override
	public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode,  String gameType, BigDecimal amount, String transferNo, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			/**
			 * 1、如果账号不存在，则会创建游戏账号，再依次上分，进入游戏。
			 * 2、如果账号已存在，则依次上分，进入游戏。
			 * 3、如果调用时不传递金额，则忽略上分，直接开始进入游戏。
			 */
			ProxyConfig config = this.getProxyConfig("playGame");
			if (null == config) {
				return this.buildResult(LiveCode._MISSING_PARAM.getCode());
			}
			amount = formatAmount(amount);
			String language = LanguageCode.getLanguage(lang);
			Integer device = DeviceTypeCode.getDeviceType(deviceType);
			ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
			dto.setLoginName(this.formatLoginName(config, loginName));
			dto.setLoginPassword(LiveCode._DEFAULT_PWD.getCode());
			dto.setNickName(loginName);
			dto.setDeviceType(device);
			dto.setGameTypeId(gameCode);
			dto.setLang(Integer.parseInt(language));
			dto.setAmount(amount);
			dto.setTransferNo(transferNo);
			dto.setBackurl("http://45.203.97.62");
			dto.setTimestamp(timestamp);
			respResult = this.handler(dto, config, GAME_URL_KEY);
			log.info("【进入游戏】平台：YABOZR,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(LiveCode._EXCEPTION.getCode());
			log.error("【进入游戏】平台：YABOZR,账号：{} , 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}
}

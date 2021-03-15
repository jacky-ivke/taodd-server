package com.esports.external.handler.xint.avia;


import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.avia.dto.*;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.utils.JsonUtil;
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
import java.util.SortedMap;

@Slf4j
public class XintAviaProxy extends AbstractTemplate {

	private final static String TYPE = "AVIA";
	
	private final static String BALANCE_KEY = "Money";
	
	private final static String GAME_URL_KEY = "Url";
	
	private final static String TRANSFER_IN = "IN";
	
	private final static String TRANSFER_OUT = "OUT";
	
	private final static String RESULT_INFO_KEY = "info";
	
	private final static String ORDER_STATUS_KEY = "Status";
	
	private final static String ORDER_AMOUNT_KEY = "Money";
	
	private final static String ERROR_KEY = "Error";
	
	private final ProxyConfigManager proxyConfigManager;
	
	public XintAviaProxy() {
		proxyConfigManager = (ProxyConfigManager) SpringUtil.getBean("proxyConfigManager");
	}
	
	protected ProxyConfig getProxyConfig(String apiType) {
		String merchantType = MerchantCode._XT.getCode();
		String type = TYPE;
		ProxyConfig config = proxyConfigManager.getConfig(merchantType, type, apiType);
		return config;
	}
	
	protected RespResultDTO buildResult(String errorCode) {
		RespResultDTO respResult = new RespResultDTO();
		respResult.setSuccess(Boolean.FALSE);
		respResult.setCode(errorCode);
		respResult.setMessage(XintAviaCode.getMessage(errorCode));
		return respResult;
	}
	
	protected Map<String, Object> getHeaders(String aesKey, String lang){
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", aesKey);
        headers.put("Content-Language", lang);
		return headers;
	}
	
	protected RespResultDTO adapter(String json, String dataKey, String... ignoreCodes) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		RespResultDTO respResult = new RespResultDTO();
		String code = this.getCode(json);
		boolean ignore = (null != ignoreCodes && Arrays.asList(ignoreCodes).contains(code));
		String ok = XintAviaCode._SUCCESS.getCode();
		respResult.setSuccess(ok.equals(code) || ignore? Boolean.TRUE : Boolean.FALSE);
		respResult.setCode(ok.equals(code) || ignore ? String.valueOf(HttpStatus.SC_OK) : code);
		respResult.setData(this.getData(json, dataKey));
		respResult.setMessage(XintAviaCode.getMessage(code));
		respResult.setSource(json);
		return respResult;
	}
	
	protected String getCode(String json) {
		String errorCode = "1";
		JSONObject jsonObj = JSONObject.fromObject(json);
		JSONObject infoObj = null != jsonObj && jsonObj.containsKey(RESULT_INFO_KEY)? jsonObj.getJSONObject(RESULT_INFO_KEY) : null;
		if(null == infoObj) {
			return errorCode;
		}
		errorCode = infoObj.containsKey(ERROR_KEY)? infoObj.getString(ERROR_KEY):errorCode;
		return errorCode;
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
	
	protected Object getData(String json, String dataKey) {
		JSONObject obj = JSONObject.fromObject(json);
		if(null == obj) {
			return null;
		}
		JSONObject data = obj.containsKey(RESULT_INFO_KEY)? obj.getJSONObject(RESULT_INFO_KEY) : null;
		if(StringUtils.isEmpty(dataKey)) {
			return data;
		}
		if(null == data) {
			return null;
		}
		Object dataValue = data.containsKey(dataKey)? data.getString(dataKey) : data;
		return dataValue;
	}
	
	protected String json2UrlParams(Object dto, ProxyConfig config) {
		SortedMap<String, Object> map = JsonUtil.object2Map(dto);
		String reqUrl = this.map2UrlParams(config, map);
		return reqUrl;
	}
	
	protected String map2UrlParams(ProxyConfig config, SortedMap<String, Object> map) {
		String url = config.getRquestUrl();
		StringBuffer urlParams = new StringBuffer();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			String key = entry.getKey();
			Object value = null != entry.getValue()? entry.getValue() : "";
			urlParams.append(key).append("=").append(value).append("&");
		}
		urlParams.deleteCharAt(urlParams.length()-1);
		String reqUrl = new StringBuffer(url).append(urlParams).toString();
		return reqUrl;
	}
	
	protected RespResultDTO handler(Integer lang, Object dto, ProxyConfig config, String dataKey, String... ignoreCodes) throws Exception {
		SortedMap<String, Object> sortedMap = JsonUtil.object2Map(dto);
		String aesKey = config.getAesKey();
		String Language = LanguageCode.getLanguage(lang);
		String url = config.getRquestUrl();
		String json = HttpClientUtils.post(url, this.getHeaders(aesKey, Language), sortedMap);
		RespResultDTO respResult = this.adapter(json, dataKey, ignoreCodes);
		return respResult;
	}
	
	protected RespResultDTO create(String loginName, Integer lang) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("create");
			if (null == config || StringUtils.isEmpty(loginName)) {
				return this.buildResult(XintAviaCode._PARAM_ERROR.getCode());
			}
			ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
			dto.setUserName(loginName);
			dto.setPassword(XintAviaCode._DEFAULT_PWD.getCode());
			dto.setCurrency("CNY");
			respResult = this.handler(lang, dto, config, null, XintAviaCode._ACCOUNT_EXISTS.getCode());
			log.info("【创建账号】平台：XINTAVIA,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAviaCode._EXCEPTION.getCode());
			log.error("【创建账号】平台：XINTAVIA,账号：{}, 异常信息:{}", loginName, e.getMessage());
		}
		return respResult;
	}

	@Override
	public RespResultDTO balance(String loginName, String ip) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("balance");
			if(null == config) {
				return this.buildResult(XintAviaCode._PARAM_ERROR.getCode());
			}
			ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
			dto.setUserName(loginName);
			respResult = this.handler(null, dto, config, BALANCE_KEY);
			respResult = this.buildBalanceResult(respResult, XintAviaCode._PARAM_ERROR.getCode());
			log.info("【获取余额】平台：XINTAVIA,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAviaCode._EXCEPTION.getCode());
			log.error("【获取余额】平台：XINTAVIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}

	@Override
	public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip,Long timestamp) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("deposit");
			if(null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO)<=0) {
				return this.buildResult(XintAviaCode._PARAM_ERROR.getCode());
			}
			ReqTransferDTO dto = new ReqTransferDTO();
			dto.setUserName(loginName);
			dto.setType(TRANSFER_IN);
			dto.setMoney(formatAmount(amount));
			dto.setId(orderNo);
			//其他币种后期扩展
			dto.setCurrency("CNY");
			respResult = this.handler(lang, dto, config, null);
			log.info("【转账上分】平台：XINTAVIA, 账号：{}, 结果：{}", loginName, respResult);
			if(StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
				return respResult;
			}
			boolean success = this.check(respResult, XintAviaCode._ACCOUNT_NOT_EXISTS.getCode());
			if (!success) {
				return respResult;
			}
			//玩家额度由商户转移至平台，如果账号不存在则新建账号
			respResult = this.create(loginName, lang);
			success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if (success) {
				respResult = deposit(loginName, lang, orderNo, amount, deviceType, ip, timestamp);
				return respResult;
			}
		} catch (Exception e) {
			log.error("【转账上分】平台：XINTAVIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
			if(null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO)<=0) {
				return this.buildResult(XintAviaCode._PARAM_ERROR.getCode());
			}
			ReqTransferDTO dto = new ReqTransferDTO();
			dto.setUserName(loginName);
			dto.setType(TRANSFER_OUT);
			dto.setMoney(formatAmount(amount));
			dto.setId(orderNo);
			//其他币种后期扩展
			dto.setCurrency("CNY");
			respResult = this.handler(lang, dto, config, null);
			log.info("【转账下分】平台：XINTAVIA, 账号：{}, 结果：{}", loginName, respResult);
		} catch (Exception e) {
			log.error("【转账下分】平台：XINTAVIA, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		} finally {
			respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
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
			if(null == config || StringUtils.isEmpty(orderNo)) {
				return this.buildResult(XintAviaCode._PARAM_ERROR.getCode());
			}
			ReqGetTransferDTO dto = new ReqGetTransferDTO();
			dto.setOrderNo(orderNo);
			dto.setCurrency("CNY");
			respResult = this.handler(lang, dto, config, ORDER_STATUS_KEY);
			log.info("【转账查询】平台：XINTAVIA, 订单号：{}, 结果：{}", orderNo, respResult);
			String status = respResult.getData().toString();
			String errorCode = respResult.getCode();
			if(!respResult.getSuccess() || !XintAviaCode.OrderStatus.SUCCESS.getCode().equals(status)) {
				errorCode = StringUtils.isEmpty(errorCode)? XintAviaCode._ORDER_FAILURE.getCode() : errorCode;
				respResult = this.buildResult(errorCode);
			}
			String source = respResult.getSource();
			respResult.setData(this.getTransferAmount(source));
		} catch (Exception e) {
			respResult = this.buildResult(XintAviaCode._EXCEPTION.getCode());
			log.error("【转账查询】平台：XINTAVIA, 订单号：{}, 异常信息：{}", orderNo, e.getMessage());
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
		JSONObject obj = json.containsKey(RESULT_INFO_KEY)? json.getJSONObject(RESULT_INFO_KEY):null;
		if(null == obj) {
			return amount;
		}
		amount = obj.containsKey(ORDER_AMOUNT_KEY)? new BigDecimal(obj.getString(ORDER_AMOUNT_KEY)) : amount;
		return amount;
	}

	@Override
	public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo,Long timestamp) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("playGame");
			if (null == config) {
				return this.buildResult(XintAviaCode._PARAM_ERROR.getCode());
			}
			
			if(null != amount && amount.compareTo(BigDecimal.ZERO)>0) {
				this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp,timestamp);
			}
			
			ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
			dto.setUserName(loginName);
			dto.setCateId(gameCode);
			respResult = this.handler(lang, dto, config, GAME_URL_KEY);
			log.info("【进入游戏】平台：XINTAVIA, 账号：{}, 结果：{}", loginName, respResult);
			boolean success = this.check(respResult, XintAviaCode._ACCOUNT_NOT_EXISTS.getCode());
			if (!success) {
				return respResult;
			}
			respResult = this.create(loginName, lang);
			if(StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
				return respResult;
			}
			success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if (success) {
				respResult = this.playGame(loginName, deviceType, memberIp, lang, gameCode, gameType, amount, transferNo,timestamp);
				return respResult;
			}
		} catch (Exception e) {
			respResult = this.buildResult(XintAviaCode._EXCEPTION.getCode());
			log.error("【进入游戏】平台：XINTAVIA,账号：{} , 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}
}

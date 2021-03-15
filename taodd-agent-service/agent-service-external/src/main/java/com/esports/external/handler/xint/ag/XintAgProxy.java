package com.esports.external.handler.xint.ag;

import com.esports.external.handler.xint.ag.dto.ReqPrepareTransferDTO;
import com.esports.external.proxy.bean.db1.ProxyConfig;
import com.esports.external.proxy.service.ProxyConfigManager;
import com.esports.network.HttpClientUtils;
import com.esports.external.handler.AbstractTemplate;
import com.esports.external.handler.MerchantCode;
import com.esports.external.handler.RespResultDTO;
import com.esports.external.handler.xint.ag.dto.*;
import com.esports.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
public class XintAgProxy extends AbstractTemplate {

	private final static String TYPE = "AG";
	
	private final static String DATA_KEY = "info";
	
	private final static String ERROR_KEY = "key_error";
	
	private final static String TRANSFER_IN = "IN";
	
	private final static String TRANSFER_OUT = "OUT";
	
	private final static String PREFIX_KEY = "test";
	
	private final ProxyConfigManager proxyConfigManager;
	
	
	public XintAgProxy() {
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
		respResult.setMessage(XintAgCode.getMessage(errorCode));
		return respResult;
	}
	
	protected String sign(Object object, String aesKey) throws Exception {
		String source = JsonUtil.object2String(object);
		String params = source.replace("{","").replace("}","").replace(":","=").replace(",","/\\\\\\\\/").replace("\"", "");
		String sign = DESUtils.encrypt(params, aesKey);
		return sign;
	}
	
	protected String encryption(Object object, ProxyConfig config) {
		String md5Key = config.getMd5key().trim();
		String aesKey=  config.getAesKey().trim();
		String params = "";
		try {
			String signature = this.sign(object, aesKey);
			String key = Md5Utils.getMD5(signature+md5Key).toLowerCase();
			RequestParamDTO model = new RequestParamDTO();
			model.setParams(signature);
			model.setKey(key);
			params = JsonUtil.object2String(model);
		} catch (Exception e) {
			params = "";
			e.printStackTrace();
		}
		return params;
	}
	
	protected String json2UrlParams(Object object, ProxyConfig config) {
		String json = this.encryption(object, config);
		SortedMap<String, Object> map = JsonUtil.string2Map(json);
		String reqUrl = this.map2UrlParams(config, map);
		return reqUrl;
	}
	
	public Map<String, Object> getHeards(ProxyConfig config){
		String merchantCode = config.getMerchantCode().trim();
		Map<String, Object> headers = new HashMap<String, Object>();
		String value = "WEB_LIB_GI_" + merchantCode;
        headers.put("User-Agent", value);
		return headers;
	}
	
	public Map<String, Object> getHeaders(){
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		return headers;
	}
	
	private String xml2Json(String xml) {
		if(StringUtils.isEmpty(xml)) {
			return "";
		}
		XMLSerializer xmlSerializer = new XMLSerializer();
		String str = xmlSerializer.read(xml).toString();
		JSONObject results = JSONObject.fromObject(str);
		String json = results.toString();
		json = json.replace("@", "");
		return json;
	}
	
	protected RespResultDTO handler(Object dto, ProxyConfig config, String dataKey) throws Exception {
		String url = this.json2UrlParams(dto, config);
		String xml = HttpClientUtils.post(url, this.getHeards(config), null);
		String json = this.xml2Json(xml);
		RespResultDTO respResult = this.adapter(json, dataKey);
		return respResult;
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
	
	protected RespResultDTO handlerGet(Object dto, ProxyConfig config, String dataKey, String key) {
		SortedMap<String, Object> map = JsonUtil.object2Map(dto);
		String reqUrl = this.map2UrlParams(config, map);
		reqUrl = reqUrl.replace(" ", "%20");
		String xml = HttpClientUtils.get(reqUrl);
		String json = this.xml2Json(xml);
		RespResultDTO respResult = this.adapter(json, dataKey);
		return respResult;
	}
	
	protected RespResultDTO adapter(String json, String dataKey) {
		RespResultDTO respResult = new RespResultDTO();
		if (StringUtils.isEmpty(json) || !isJsonObject(json)) {
			return respResult;
		}
		JSONObject jsonObj = JSONObject.fromObject(json);
		String code = null != jsonObj && jsonObj.containsKey(ERROR_KEY)? jsonObj.getString(ERROR_KEY): XintAgCode._SUCCESS.getCode();
		String ok = XintAgCode._SUCCESS.getCode();
		respResult.setSuccess(ok.equals(code) ? Boolean.TRUE : Boolean.FALSE);
		respResult.setCode(ok.equals(code) ? String.valueOf(HttpStatus.SC_OK) : code);
		respResult.setData(this.getData(json, dataKey));
		respResult.setMessage(XintAgCode.getMessage(code));
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
	
	protected String createOrderId(String merchantCode, String transferNo) {
		StringBuffer sb = new StringBuffer();
		String random = RandomUtil.getUUID(merchantCode);
		sb = StringUtils.isEmpty(transferNo)? sb.append(random) : sb.append(merchantCode).append(transferNo);
		return sb.toString();
	}
	
	protected String formatLoginName(String loginName) {
		StringBuffer sb = new StringBuffer(PREFIX_KEY).append("_").append(loginName);
		return sb.toString();
	}
	
	public static String unFormatLoginName(String loginName) {
		String prefix = new StringBuffer(PREFIX_KEY).append("_").toString();
		loginName =loginName.replace(prefix, "");
		return loginName;
	}
	
	protected RespResultDTO create(String loginName, Integer lang) {
		/**
		 * 检测并创建游戏账号
		 * 1、如果該账号及密码与数据库纪录吻合, 系统返回 0;
		 * 2、如果该账號不存在，系统将會創建一个新的游戏账号並返回 0
		 */
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("create");
			if (null == config || StringUtils.isEmpty(loginName)) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			ReqCreatePlayerDTO dto = new ReqCreatePlayerDTO();
			dto.setCagent(config.getMerchantCode().trim());
			dto.setLoginname(this.formatLoginName(loginName));
			dto.setPassword(XintAgCode._DEFAULT_PWD.getCode());
			respResult = this.handler(dto, config, null);
			log.info("【创建账号】平台：XINTAG,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAgCode._EXCEPTION.getCode());
			log.error("【创建账号】平台:XINTAG,账号：{}, 异常信息:{}", loginName, e.getMessage());
		}
		return respResult;
	}

	@Override
	public RespResultDTO balance(String loginName, String ip) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("balance");
			if(null == config || StringUtils.isEmpty(loginName)) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			ReqGetBalanceDTO dto = new ReqGetBalanceDTO();
			dto.setCagent(config.getMerchantCode().trim());
			dto.setLoginname(this.formatLoginName(loginName));
			respResult = this.handler(dto, config, null);
			respResult = this.buildBalanceResult(respResult, XintAgCode._PARAM_ERROR.getCode());
			log.info("【获取余额】平台：XINTAG,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAgCode._EXCEPTION.getCode());
			log.error("【获取余额】平台：XINTAG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}
	
	protected RespResultDTO prepareTransfer(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, String transferType) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("prepareTransfer");
			if(null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			ReqPrepareTransferDTO dto = new ReqPrepareTransferDTO();
			String merchantCode = config.getMerchantCode().trim();
			String billNo = this.createOrderId(merchantCode, orderNo);
			dto.setCagent(merchantCode);
			dto.setLoginname(this.formatLoginName(loginName));
			dto.setPassword(XintAgCode._DEFAULT_PWD.getCode());
			dto.setBillno(billNo);
			dto.setType(transferType);
			dto.setCredit(amount.setScale(2, BigDecimal.ROUND_DOWN));
			respResult = this.handler(dto, config, null);
			
			boolean success = this.check(respResult, XintAgCode._ACCOUNT_NOT_EXISTS.getCode());
			if (!success) {
				return respResult;
			}
			//玩家登录游戏，如果账号不存在则新建账号
			respResult = this.create(loginName, lang);
			success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if (success) {
				respResult = this.prepareTransfer(loginName, lang, orderNo, amount, deviceType, ip, transferType);
				return respResult;
			}
			log.info("【预备转账】平台：XINTAG,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAgCode._EXCEPTION.getCode());
			log.error("【预备转账】平台：XINTAG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}

	@Override
	public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			respResult = this.prepareTransfer(loginName, lang, orderNo, amount, deviceType, ip, TRANSFER_IN);
			boolean success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if(!success) {
				return respResult;
			}
			ProxyConfig config = this.getProxyConfig("deposit");
			if(null == config || StringUtils.isEmpty(loginName) || null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			ReqTransferDTO dto = new ReqTransferDTO();
			String merchantCode = config.getMerchantCode().trim();
			String billNo = this.createOrderId(merchantCode, orderNo);
			dto.setCagent(merchantCode);
			dto.setLoginname(this.formatLoginName(loginName));
			dto.setPassword(XintAgCode._DEFAULT_PWD.getCode());
			dto.setBillno(billNo);
			dto.setType(TRANSFER_IN);
			dto.setCredit(formatAmount(amount));
			respResult = this.handler(dto, config, null);
			log.info("【转账上分】平台：XINTAG,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			log.error("【转账上分】平台：XINTAG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		} finally {
			respResult = this.checkTransfer(respResult, loginName, lang, orderNo, amount, timestamp);
		}
		return respResult;
	}

	@Override
	public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			respResult = this.prepareTransfer(loginName, lang, orderNo, amount, deviceType, ip, TRANSFER_OUT);
			boolean success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if(!success) {
				return respResult;
			}
			ProxyConfig config = this.getProxyConfig("draw");
			if(null == config) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			ReqTransferDTO dto = new ReqTransferDTO();
			String merchantCode = config.getMerchantCode().trim();
			String billNo = this.createOrderId(merchantCode, orderNo);
			dto.setCagent(merchantCode);
			dto.setLoginname(this.formatLoginName(loginName));
			dto.setPassword(XintAgCode._DEFAULT_PWD.getCode());
			dto.setBillno(billNo);
			dto.setType(TRANSFER_OUT);
			dto.setCredit(formatAmount(amount));
			respResult = this.handler(dto, config, null);
			log.info("【转账下分】平台：XINTAG,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			log.error("【转账下分】平台：XINTAG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
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
		BigDecimal transferAmount = null == dto.getData()? formatAmount(amount) : new BigDecimal(dto.getData().toString());
		dto.setData(transferAmount);
		return dto;
	}

	@Override
	public RespResultDTO searchOrder(String loginName, String orderNo, Integer lang, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("searchOrder");
			if(null == config || StringUtils.isEmpty(orderNo)) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			ReqGetTransferDTO dto = new ReqGetTransferDTO();
			String merchantCode = config.getMerchantCode().trim();
			String billNo = this.createOrderId(merchantCode, orderNo);
			dto.setCagent(merchantCode);
			dto.setBillno(billNo);
			respResult = this.handler(dto, config, null);
			respResult.setData(this.getTransferAmount(respResult));
			log.info("【转账订单查询】平台：XINTAG, 订单号：{}, 结果：{}", orderNo, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAgCode._EXCEPTION.getCode());
			log.error("【转账订单查询】平台：XINTAG, 订单号：{}, 异常信息：{}", orderNo, e.getMessage());
		}
		return respResult;
	}

	private BigDecimal getTransferAmount(RespResultDTO respResult) {
		BigDecimal amount = BigDecimal.ZERO;
		
		return amount;
	}
	
	@Override
	public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {
		RespResultDTO respResult = null;
		try {
			ProxyConfig config = this.getProxyConfig("playGame");
			if(null == config) {
				return this.buildResult(XintAgCode._PARAM_ERROR.getCode());
			}
			
			respResult = this.create(loginName, lang);
			if(StringUtils.isEmpty(respResult.getCode()) && ObjectUtils.isEmpty(respResult.getData())) {
				return respResult;
			}
			boolean success = this.check(respResult, String.valueOf(HttpStatus.SC_OK));
			if(!success) {
				return respResult;
			}
			
			if(null != amount && amount.compareTo(BigDecimal.ZERO)>0) {
				this.deposit(loginName, lang, transferNo, amount, deviceType, memberIp, timestamp);
			}
			
			String language = LanguageCode.getLanguage(lang);
			ReqCreateDepositLoginPlayerDTO dto = new ReqCreateDepositLoginPlayerDTO();
			dto.setCagent(config.getMerchantCode().trim());
			dto.setLoginname(this.formatLoginName(loginName));
			dto.setPassword(XintAgCode._DEFAULT_PWD.getCode());
			//每次进入游戏，必须保证sid唯一且之前未被占用
			dto.setSid(this.createOrderId(config.getMerchantCode(), ""));
			dto.setLang(language);
			dto.setGameType(gameCode);
			respResult.setData(this.json2UrlParams(dto, config));
			log.info("【进入游戏】平台：XINTAG,账号：{},结果：{}", loginName, respResult);
		} catch (Exception e) {
			respResult = this.buildResult(XintAgCode._EXCEPTION.getCode());
			log.error("【进入游戏】平台：XINTAG, 账号：{}, 异常信息：{}", loginName, e.getMessage());
		}
		return respResult;
	}
}

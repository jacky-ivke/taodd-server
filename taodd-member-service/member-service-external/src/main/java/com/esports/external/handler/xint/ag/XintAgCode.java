package com.esports.external.handler.xint.ag;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public enum XintAgCode {
	
	_DEFAULT_PWD("YaboAg@123","默认密码"),
	
	_SUCCESS("0", "成功"),
	
	_EXCEPTION("-1", "异常"),
	
	_KEY_ERROR("44000","Key 错误,请检查明码及加密顺序是否正确"),
	
	_SEARCH_BALANCE_SUCCESS("64010", "查询额成功"),
	
	_AGENT_NOT_FOUND("61003", "产品ID或CAGENT不存在"),
	
	_ACCOUNT_NOT_EXISTS("60001","用户不存在"),
	
	_INF_ERROR("61004","执行错误"),
	
	_SEARCH_TIME_LIMIT("61001","查询请求时间被限制"),
	
	_PARAM_ERROR("61002","参数缺失，请检查参数是否正确"),
	
	_AGENT_NOT_EXISTS("61005","代理商不存在"),
	
	_INFO_KEY_ERROR("key_error", "Key值错误"),
	
	_INFO_NETWORK_ERROR("network_error", "网络问题导致资料遗失"),
	
	_INFO_ACCOUNT_ADD_FAIL("account_add_fail","创建新账号失败, 可能是密码不正确或账号已存在"),
	
	_INFO_ERROR("error","其他错误"),
	
	;
	
	private String code;
	
	private String msg;
	
	XintAgCode(String code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public static String getMessage(String errorCode) {
		String msg = errorCode;
		if (StringUtils.isEmpty(errorCode)) {
			return msg;
		}
		for (XintAgCode code : XintAgCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}

	public static Integer getOkStatus(Integer betFlag) {
		if(0==betFlag) {
			return OrderCode._PENDING.getCode();
		}
		if(1==betFlag) {
			return OrderCode._SUCCESS.getCode();
		}
		if(-8 == betFlag || -9 == betFlag) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getResult(Integer betFlag, BigDecimal profitAmount) {
		if(1==betFlag && BigDecimal.ZERO.compareTo(profitAmount)<0) {
			return BetResultCode._WIN.getCode();
		}
		if(1==betFlag && BigDecimal.ZERO.compareTo(profitAmount)>0) {
			return BetResultCode._LOSS.getCode();
		}
		if(1==betFlag && BigDecimal.ZERO.compareTo(profitAmount)==0) {
			return BetResultCode._DRAW.getCode();
		}
		if(-8 == betFlag || -9 == betFlag) {
			return BetResultCode._CANCELLED.getCode();
		}
		return "";
	}
	
}

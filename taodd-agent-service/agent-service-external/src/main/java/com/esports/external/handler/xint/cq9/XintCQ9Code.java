package com.esports.external.handler.xint.cq9;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public enum XintCQ9Code {
	
	_DEFAULT_PWD("XintCQ9123","默认密码"),

	_SUCCESS("0", "成功"),
	
	_EXCEPTION("-1","异常"),
	
	_PARAM_ERROR("5","参数错误"),
	
	_BALANCE_NOT_ENOUGH("1", "余额不足"),
	
	_ACCOUNT_NOT_EXISTS("2", "用户不存在"),
	
	_TOKEN_INVALID("3","token无效"),
	
	_AUTHORIZATION_INVALID("4", "认证无效"),
	
	_ACCOUNT_EXISTS("6","用户已存在"),
	
	_METHOD_NOT_ALLOWED("7","不允许的方法"),
	
	_DATA_NOT_FOUND("8","找不到数据"),
	
	_MTCODE_DUPLICATE("9","mtCode重复"),
	
	_TIME_FORMAT_ERROR("10","时间格式错误"),
	
	_QUERY_TIME_OUTOFRANGE("11","查询时间超出范围"),
	
	_TIME_ZONE_MUSTBET_UTC4("12","时区必须是utc-4"),
	
	_GAME_NOT_FOUND("13","未找到游戏"),
	
	_ACCOUNT_PASSWORD_ERROR("14","账号或密码错误"),
	
	_ACCOUNT_PASSWORD_FORMAT_ERROR("15","账号和密码格式错误"),
	
	_GAME_UNDER_MAINTENANCE("23","游戏维护中"),
	
	_ACCOUNT_TOO_LONG("24","用户账号太长"),
	
	_CURRENCY_NOT_SUPPORT("28","币种不支持"),
	
	_NO_DEFAULT_POOL_TYPE("29", "无默认池类型"),
	
	_POOL_UNINITIALIZE("30", "池初始化错误"),
	
	_CURRENCY_NOT_MATCH_AGENT("31","货币与代理的货币不匹配"),
	
	_TRANSACTION_IN_PROGRESS("33","交易正在进行中，请稍后检查。"),
	
	_INF_ERROR("100","接口错误"),
	
	_AUTH_SERVICE_ERROR("101","验证服务错误"),
	
	_USER_SERVICE_ERROR("102","用户服务错误"),
	
	_TRANSACTION_SERVICE_ERROR("103","事务服务错误"),
	
	_GAME_MANAGER_SERVICE_ERROR("104","游戏管理器服务错误"),
	
	_WALLET_SERVICE_ERROR("105","钱包服务错误"),
	
	_TVIEWER_SERVICE_ERROR("106","Tviewer服务错误"),
	
	_ORDERVIEW_SERVICE_ERROR("107","Orderview服务错误"),
	
	_REPORT_SERVICE_ERROR("108","report服务错误"),
	
	_PROMOTE_SERVICE_ERROR("110","promote服务错误"),
	
	_PROMOTERED_SERVICE_ERROR("111","promoteRed服务错误"),
	
	_LUCKYBAG_SERVICE_ERROR("112","luckybag服务错误"),
	
	_ACCOUNT_FROZEN("200","账号被冻结"),
	
	_ACCOUNT_LOCKED("202","用户被禁用"),
	
	_ORDER_FAILURE("order-error", "订单错误"),
	
	;
	private String code;
	
	private String msg;
	
	XintCQ9Code(String code, String msg){
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
		for (XintCQ9Code code : XintCQ9Code.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	public static Integer getOkStatus(String betStatus) {
		if("complete".equalsIgnoreCase(betStatus)) {
			return OrderCode._SUCCESS.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getResult(BigDecimal profitAmount) {
		if(BigDecimal.ZERO.compareTo(profitAmount)<0) {
			return BetResultCode._WIN.getCode();
		}
		if(BigDecimal.ZERO.compareTo(profitAmount)>0) {
			return BetResultCode._LOSS.getCode();
		}
		if(BigDecimal.ZERO.compareTo(profitAmount)==0) {
			return BetResultCode._DRAW.getCode();
		}
		return "";
	}
}

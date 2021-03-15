package com.esports.external.handler.yabo.slots;

import java.math.BigDecimal;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

public enum SlotsCode {
	
	_DEFAULT_PWD("YaboSlots@123","默认密码"),

	_SUCCESS("1000", "操作成功"),
	
	_EXCEPTION("-1", "异常"),

	_PARAM_ERROR("1001", "参数异常"),

	_PARAM_PARSING_ERROR("1001", "参数异常"),

	_SYSTEM_TIMEOUT("1003", "系统超时"),

	_ACCOUNT_FREEZING("2001", "当前玩家账号已经冻结, 请联系游戏客服"),

	_ACCOUNT_NOT_USE("2002", "测试账号已停用"),

	_ACCOUNT_INVALID("2003", "测试账号失效"),

	_TOKEN_ABNORMAL("2004", "token生成异常"),

	_NULL_NOT_ALLOWED("2005", "玩家数据不能为空"),

	_ACCOUNT_PASSWORD_ERROR("2006", "玩家账号密码错误"),

	_MERCHANTS_NOT_EMPTY("2007", "商户编号不允许为空"),

	_MERCHANTS_ERROR("2008", "商户编号错误"),

	_MERCHANTS_NOT_CONFIGURED("2009", "请配置商户相关信息"),

	_MD5_SIGNATURE_ERROR("2010", "参数MD5签名失败"),

	_DEPOSIT_ACCOUNT_ILLEGAL("3001", "上分失败,非法玩家"),

	_SERCH_MERCHANTS_ERROR("3002", "上分失败, 获取商户信息失败"),

	_DEPOSIT_ACCOUNT_STATUS_ABNORMAL("3003", "上分失败, 玩家状态不正常"),

	_DEPOSIT_REQUEST_TIMEOUT("3004", "上分失败,请求超时"),

	_DEPOSIT_ORDER_EXISTS("3005", "上分失败,订单已存在"),

	_DEPOSIT_ACCOUNT_PASSWORD_ERROR("3006", "上分失败,玩家账号的密码校验失败"),

	_DEPOSIT_AMOUNT_LESSTHAN_ZERO("3007", "上分失败,额度小于 0"),

	_DEPOSIT_PARAM_MUST_REQUIRED_ERROR("3008", "上分失败,必填项校验失败"),

	_DRAW_ORDER_EXISTS("3010", "下分订单已存在"),

	_DRAW_ACCOUNT_ILLEGAL("3011", "玩家不存在"),

	_DRAW_ACCOUNT_STATUS_ABNORMAL("3012", "玩家状态不正常"),

	_DRAW_ACCOUNT_NOT_EXISTS("3013", "下分失败,玩家不存在"),

	_DRAW_BALANCE_NOT_ENOUGH("3014", "玩家余额不足"),

	_DRAW_ACCCOUNT_IN_THE_GAME("3015", "下分失败:玩家正在游戏中"),

	_DRAW_ORDERNO_ERROR("3016", "下分失败,订单号校验失败"),

	_DRAW_AMOUNT_LESSTHAN_ZERO("3017", "下分失败,额度小于 0"),

	_DRAW_ACCOUNT_PASSWORD_ERROR("3018", "下分失败,玩家账号的密码校验失败"),

	_ORDER_STATUS_ERROR("4001", "查询订单状态,订单状态失败"),

	_ORDER_NOT_EXISTS("4002", "查询订单状态, 无此订单编号"),

	_CHECK_BALANCE_ACCOUNT_NOT_EXISTS("5001", "查询余额，玩家不存在"),

	_CHECK_BALANCE_NOT_EXISTS("5002", "查询余额，玩家余额不存在"),
	
	_MODIFY_PWD_ACCOUNT_NOT_EXISTS("6001","修改密码，无此玩家"),
	
	_MODIFY_PWD_OLD_ERROR("6002","修改密码，旧密码对比失败"),
	
	_MODIFY_PWD_ERROR("6003","修改密码，更新密码失败"),
	
	_REQUEST_FREQUENTLY("7001","查询请求过于频繁"),
	
	_SEARCH_TIME_OUT("7002","查询时间超过规范时间"),
	
	_SEARCH_TIME_SECTION_TOO_LARGE("7003","查询时间区间过长"),
	
	_SEARCH_PAGE_DATA_ERROR("7004","每页条数要在 1000 到 10000 之间"),
	
	_PLAYER_IN_THE_GAME("8001","玩家在其它游戏中"),
	
	_GAMEID_NOT_EXISTS("8002","游戏（gameId）不存在"),
	
	_GAMEID_REPEAT("8003","游戏列表 gameId 重复"),
	
	_ORDER_FAILURE("order-error", "订单错误")
	;

	private String code;

	private String message;

	SlotsCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static String getMessage(String errorCode) {
		String msg = errorCode;
		if (StringUtils.isEmpty(errorCode)) {
			return msg;
		}
		for (SlotsCode code : SlotsCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMessage();
			}
		}
		return msg;
	}
	
	public enum OrderStatus{
		_SUCCESS("0", "成功");
		private String code;
		private String msg;
		OrderStatus(String code, String msg){
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
	}
	
	public static Integer getOkStatus() {
		return OrderCode._SUCCESS.getCode();
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

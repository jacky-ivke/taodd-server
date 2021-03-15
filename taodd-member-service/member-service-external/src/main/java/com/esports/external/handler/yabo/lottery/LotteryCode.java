package com.esports.external.handler.yabo.lottery;

import java.math.BigDecimal;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

public enum LotteryCode {
	
	_DEFAULT_PWD("YaboLottery@123","默认密码"),

	_SUCCESS("0", "成功"),
	
	_EXCEPTION("-1", "异常"),
	
	_FAILED("500", "失败/请联系管理员"),
	
	_PERMISSION_DENIED("401", "权限不足"),
	
	_PARAM_ERROR("405","参数异常、参数转换异常"),
	
	_ACCOUNT_EXISTS("701","会员已注册，请不要重复注册"),
	
	_SEARCH_MERCHANT_AWARD_ERROR("702","查询商户奖金组数据失败"),
	
	_SEARCH_MERCHANT_DOUBLE_ERROR("703","查询商户双面盘奖金组数据失败"),
	
	_SEARCH_MERCHANT_NORMAL_ERROR("704","查询商户标准盘奖金组数据失败"),
	
	_MERCHANT_DOUBLE_POINT_RANGE_ERROR("705","商户双面盘代理模式下上送的彩种返点数据不在维护区间内"),
	
	_MERCHANT_NORMAL_POINT_RANGE_ERROR("706","商户标准盘代理模式下上送的彩种返点数据不在维护区间内"),
	
	_CHECK_SIGNATURE_ERROR("707","验证签名失败，请检查签名生成规则是否合法"),
	
	_MODIFY_MEMBER_ERROR("708","修改会员信息失败，请检查是否注册过该会员"),
	
	_LOGIN_ERROR("709","登录失败，请检查是否注册该会员"),
	
	_MERCHANT_DOUBLE_POINT_ERROR("710","商户双面盘代理模式下上送的彩种返点不合法"),
	
	_MERCHANT_NORMAL_POINT_ERROR("711","商户标准盘代理模式下上送的彩种返点不合法"),
	
	_KICK_LINE_ERROR("712","踢线失败，请检查是否注册该会员"),
	
	_ACCOUNT_PASSWORD_ERROR("713","会员登录密码错误，请检查会员维护的密码是否一致"),
	
	_TRANSFER_ACCOUNT_ERROR("714","转账失败，请检查是否注册该会员"),
	
	_TRANSFER_BALANCE_ERROR("715","转出失败，转出余额不足"),
	
	_TRANSFER_ORDERNO_ERROR("716","会员转入转出记录重复，请检查"),
	
	_SYSTEM_ERROR("717", "系统系统，请稍后再试"),
	
	_MERCHANT_BALANCE_NOT_ENOUGH("1001","余额不足，请充值"),
	
	_UPDATE_BALANCE_ERROR("1002","更新余额失败"),
	
	_TRADE_TYPE_ERROR("1003","交易类型参数有误"),
	
	_MERCHANT_ERROR("1004","参数商户账号:merchant不合法"),
	
	_MEMBER_ERROR("1005", "参数会员账号:member 不合法"),
	
	_ORDER_FAILURE("order-error", "订单错误")
	
	;

	private String code;

	private String msg;

	LotteryCode(String code, String msg) {
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
		for (LotteryCode code : LotteryCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
	}
	
	public enum OrderStatus{
    	WAIT_AWARD("1", "待开奖"),
    	NOT_WIN("2","未中奖"),
    	WON_PRIZE("3","已中将"),
    	HANG_UP("4","挂起"),
    	SETTLEMENT("5","结算");
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
		
		public static String getMessage(String errorCode) {
			String msg = errorCode;
			if (StringUtils.isEmpty(errorCode)) {
				return msg;
			}
			for (OrderStatus code : OrderStatus.values()) {
				if (code.getCode().equalsIgnoreCase(errorCode)) {
					return code.getMsg();
				}
			}
			return msg;
		}
	}
	public static Integer getOkStatus(Integer betStatus, Boolean cancelStatus) {
		if(!cancelStatus && 1 == betStatus) {
			return OrderCode._PENDING.getCode();
		}
		if(!cancelStatus && 1!= betStatus) {
			return OrderCode._SUCCESS.getCode();
		}
		if(cancelStatus) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getResult(boolean cancelStatus, BigDecimal profitAmount) {
		if(cancelStatus) {
			return BetResultCode._CANCELLED.getCode();
		}
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

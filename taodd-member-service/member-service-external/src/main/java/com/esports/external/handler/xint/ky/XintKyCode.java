package com.esports.external.handler.xint.ky;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public enum XintKyCode {

	
	_SUCCESS("0", "操作成功"),
	
	_EXCEPTION("-1","异常"),
	
	_TOKEN_ERROR("1", "TOKEN 丢失（重新调用登录接口获取）"),
	
	_CHANNEL_NOT_EXISTS("2","渠道不存在（请检查渠道 ID 是否正确）"),
	
	_CHECK_TIMEOUT("3", "验证时间超时（请检查 timestamp 是否正确）"),
	
	_CHECK_ERROR("4","验证错误"),
	
	_WHITE_ERROR("5","渠道白名单错误（请联系客服添加服务器白名单）"),
	
	_PARAM_REQUIRED_ERROR("6","验证字段丢失（请检查参数完整性）"),
	
	_REQUEST_TYPE_ERROR("8","不存在的请求"),
	
	_MD5_SIGNATURE_ERROR("15","渠道验证错误（1.MD5key 值是否正确；2.生成 key 值中的 timestamp 与参数中的是否一致；3. 生成 key 值中的 timestamp 与代理编号以字符串形式拼接）"),
	
	_NO_GAME_RECORD("16","数据不存在（当前没有注单）"),
	
	_ACCOUNT_NOT_USE("20","账号禁用"),
	
	_CHECK_SIGNATURE_ERROR("22","AES解密失败"),
	
	_SEARCH_TIME_SECTION_TOO_LARGE("24","渠道拉取数据超过时间范围"),
	
	_ORDER_NOT_EXISTS("26","订单号不存在"),
	
	_DB_EXCEPTION("27","数据库异常"),
	
	_IP_DISABLE("28","ip 禁用"),
	
	_ORDERNO_FORMAT_ERROR("29","订单号与订单规则不符"),
	
	_CHEKC_ONLINE_ERROR("30","获取玩家在线状态失败"),
	
	_AMOUNT_LESSTHAN_ZERO("31","更新的分数小于或者等于 0"),
	
	_MODIFY_ACCOUNT_ERROR("32","更新玩家信息失败"),
	
	_MODIFY_AMOUNT_ERROR("33","更新玩家金币失败"),
	
	_ORDERNO_REPEAT("34","订单重复"),
	
	_ACCOUNT_NOT_EXISTS("35","获取玩家信息失败（请调用登录接口创建账号）"),
	
	_KINDID_NOT_EXISTS("36","KindID 不存在"),
	
	_LOGIN_DISABLE_TRANSFER("37","登录瞬间禁止下分，导致下分失败"),
	
	_BALANCE_NOT_ENOUGH("38", "余额不足导致下分失败"),
	
	_CONCURRENT_REQUESTS("39","禁止同一账号登录带分、上分、下分并发请求，后一个请求被拒"),
	
	_TRANSFER_AMOUNT_TOO_LARGE("40","单次上下分数量不能超过一千万"),
	
	_SEARCH_TIME_SECTION_ERROR("41","拉取对局汇总统计时间范围有误"),
	
	_AGENT_DISABLED("42","代理被禁用"),
	
	_SEARCH_FREQUENTLY("43","拉单过于频繁(两次拉单时间间隔必须大于 5 秒)"),
	
	_ORDER_PROCESSING("44","订单正在处理中"),
	
	_PARAM_ERROR("45","参数错误"),
	
	_REG_ACCOUNT_EXCEPTION("1001","注册会员账号系统异常"),
	
	_AGENT_BALANCE_NOT_ENOUGH("1002","代理商金额不足"),
	
	_ORDER_FAILURE("order-error", "订单错误")
	;
	
	private String code;
	
	private String msg;
	
	XintKyCode(String code, String msg){
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
		for (XintKyCode code : XintKyCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
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

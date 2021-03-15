package com.esports.external.handler.yabo.live;

import java.math.BigDecimal;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

public enum LiveCode {
	
	_DEFAULT_PWD("YaboLive@123","默认密码"),

	_SUCCESS("200", "操作成功"),
	
	_EXCEPTION("-1", "异常"),
	
	_ACCOUNT_FORMAT_ERROR("10000", "游戏账号必须是代理号开头"),
	
	_ACCOUNT_EXISTS("20000", "游戏账号已存在"),
	
	_ACCOUNT_NOT_EXISTS("20001","游戏账号不存在"),
	
	_ACCOUNT_LENGTH_ERROR("20002","游戏账号介于 8-50 位数字/字符"),
	
	_PASSWORD_LENGTH_ERROR("20003","登陆密码介于 6-32 个字符"),
	
	_ACCOUNT_STATUS_ERROR("20005","账号投注状态介于 0 或 1"),
	
	_ACCOUNT_SPECIAL_CHARACTERS("20006","账号存在特殊字符"),
	
	_PASSWORD_SPECIAL_CHARACTERS("20007", "密码存在特殊字符"),
	
	_ACCOUNT_OUT_OF_SERVICE("20008","账号已停用"),
	
	_ACCOUNT_PASSWORD_ERROR("20009","账号密码错误"),
	
	_AMOUNT_ERROR("30000","输入金额异常，应大于 0"),
	
	_PLAYER_BALANCE_NOT_ENOUGH("30001","玩家余额不足"),
	
	_MERCHANT_BALANCE_NOT_ENOUGH("30002","商户余额不足"),
	
	_PLAYER_CAPITAL_NOT_EXISTS("30003", "玩家资金账户不存在"),
	
	_MERCHANT_CAPITAL_NOT_EXISTS("30004", "商户资金账户不存在"),
	
	_ORDERNO_LENGTH_ERROR("30005","交易单号超出长度范围：50 个字符"),
	
	_ORDERNO_EXISTS("30006","交易单号已经存在"),
	
	_ORDERNO_NOT_EXISTS("30007","交易单号不存在"),
	
	_HANDICAP_NOT_EXISTS("30008","不存在该盘口或盘口不可用"),
	
	_LANG_NOT_EXISTS("30009","不存在该语言或语言不可用"),
	
	_DEVICETYPE_NOT_EXISTS("30010","设备类型不存在，只能是 1(web 网页)和 2(手机网页)"),
	
	_ACCOUNT_ERROR("30011","会员账号有误"),
	
	_PLAYER_CAPITAL_ABNORMAL("30012","会员资金账户异常"),
	
	_PARAM_ERROR("90000", "参数错误"),
	
	_MISSING_PARAM("90001","缺少参数"),
	
	_MISSING_HEADER_PARAM("90002", "缺少请求头参数"),
	
	_TIMSTAMP_ERROR("90003", "时间格式错误,应是 yyyy-MM-dd HH:mm:ss 格式"),
	
	_DATE_ERROR("90004","时间格式错误,应是 yyyyMMdd 格式"),
	
	_TIMESTAMP_LENGTH_ERROR("90005","时间戳应是 13 位数整形"),
	
	_PAGENO_ERROR("90006","页码错误"),
	
	_SIGNATURE_ERROR("90007","请求头参数与加密参数值不匹配"),
	
	_TIME_FORMAT_ERROR("90010","时间日期异常"),
	
	_JSON_ANALYZING_ERROR("91110","json 格式解析错误"),
	
	_LOGIC_ERROR("91111","逻辑业务异常"),
	
	_ACCESS_RESTRICTIONS("92222","访问限制"),
	
	_AGENT_STATUS_NOT_AVAILABLE("93333","代理状态不可用"),
	
	_AGENT_NOT_KEY_AVAILABLE("93334","代理没有可用秘钥"),
	
	_GAME_DOMAIN_NOT_EXISTS("93335", "游戏域名不存在"), 
	
	_FREQUENT_VISITS("94444", "访问频繁"),
	
	_SYSTEM_ERROR("96666", "系统错误"),
	
	_DECRYPT_ERROR("97777","解密失败"),

	_CHECK_SIGNATURE_ERROR("97778", "验证签名失败"),
	
	_OUT_OF_SERVICE("98888","服务暂时不可用"),
	
	_UNKNOWN_ERROR("99999","其他未知错误"),
	
	_ORDER_FAILURE("order-error", "订单错误")
	;
	
	private String code;
    
    private String msg;

    LiveCode(String code, String msg){
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
        if(StringUtils.isEmpty(errorCode)){
            return msg;
        }
        for(LiveCode code : LiveCode.values()){
            if(code.getCode().equalsIgnoreCase(errorCode)){
                return code.getMsg();
            }
        }
        return msg;
    }
    
    public enum OrderStatus{
    	PENDING("2", "处理中"),
    	SUCCESS("0","成功"),
    	FAILD("1","失败"),
    	;
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
    
    public static Integer getOkStatus(Integer betStatus) {
    	if(0==betStatus) {
    		return OrderCode._PENDING.getCode();
    	}
    	if(1==betStatus) {
    		return OrderCode._SUCCESS.getCode();
    	}
    	return OrderCode._INVALID.getCode();
    }
    
    public static String getResult(Integer betFlag, BigDecimal profitAmount) {
    	if(0==betFlag && BigDecimal.ZERO.compareTo(profitAmount)<0) {
    		return BetResultCode._WIN.getCode();
    	}
    	if(0==betFlag && BigDecimal.ZERO.compareTo(profitAmount)>0) {
    		return BetResultCode._LOSS.getCode();
    	}
    	if(0==betFlag && BigDecimal.ZERO.compareTo(profitAmount)==0) {
    		return BetResultCode._DRAW.getCode();
    	}
    	if(0!=betFlag) {
    		return BetResultCode._CANCELLED.getCode();
    	}
    	return "";
    	
    }
    
}

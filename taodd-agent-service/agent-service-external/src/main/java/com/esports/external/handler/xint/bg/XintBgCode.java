package com.esports.external.handler.xint.bg;

import com.esports.constant.BetResultCode;
import com.esports.constant.OrderCode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public enum XintBgCode {

	_SUCCESS("0","成功"),
	
	_EXCEPTION("-1","异常"),
	
	_UNKNOWN_ERROR("000","未知异常"),
	
	_NETWORK_ERROR("001", "网络错误"),
	
	_SERVICE_ERROR("002", "应用层未知异常"),
	
	_ACCESS_DATA_ERROR("003", "数据访问未知异常"),
	
	_ACCESS_CACHE_ERROR("004", "缓存访问未知异常"),
	
	_INF_ERROR("005","RPC调用未知异常"),
	
	_PARAM_ERROR("100", "传入的参数不对"),
	
	_JSON_FORMAT_ERROR("101", "JSON字符串语法不正确"),
	
	_REQUEST_JSON_EMPTY("102", "请求的JSON串为空"),
	
	_JSON_TO_XML_ERROR("106", "解析json-xml流为JSON对象出错"),
	
	_CONTENT_TYPE_ERROR("108","content-type MIME unsupported"),
	
	_XML_TO_MAP_ERROR("109","XML解析成MAP对象出错"),
	
	_REQUEST_404_ERROR("110", "HTTP 404 error, action not found!"),
	
	_ACCESS_AUTHORIZATION_FAILURE("111","鉴权不通过(access authorization failure)"),
	
	_FORMAT_EXCEPTION("112","HTTP 格式化输出异常(format exception)"),
	
	_PREPROCESSOR_EXCEPTION("113","HTTP 执行Preprocessor失败(call preprocessor exception)"),
	
	_TIMEOUT("114","操作超时(operation timeout)"),
	
	_ENCRYPTE_FAILURE("115","数据加解密失败(encrypte failure)"),
	
	_COMPRESS_FAILURE("116","数据解压缩失败(compress failure)"),
	
	_HTTP_GET_EXCEPTION("117","HTTP GET方法执行异常"),
	
	_HTTP_POST_EXCEPTION("118", "HTTP POST方法执行异常"),
	
	_FTP_FILELIST_ERROR("119", "FTP 获取服务器上的文件列表异常"),
	
	_FTP_INIT_ERROR("120", "FTP 初始化连接服务器异常"),
	
	_FTP_CLOSE_ERROR("121","FTP 关闭连接异常"),
	
	_HTTP_REQUEST_EXCEPTION("125", "HTTP 请求处理异常"),
	
	_DB_TIMEOUT("301", "数据库执行超时"),
	
	_SQL_UPDATE_ERROR("302", "数据库执行(DML update) SQL语句异常"),
	
	_SQL_SELECT_ERROR("303", "数据库执行(QUERYselect) SQL语句异常"),
	
	_NOT_FOUND_DB("304", "没有找到数据库"),
	
	_DB_TRANSACTION_ERROR("305","数据库执行事务控制(transaction)的SQL语句异常"),
	
	_SQL_AFFACTED_EXCEPTION("306","据库执行SQL语句, 影响行数(affacted)异常"),
	
	_SQL_ANALYSIS_EXCEPTION("307", "解析动态SQL异常"),
	
	_TARGET_SOURCE_NOT_AVAILABLE("308", "目标数据源不可用"),
	
	_MATCH_DB_NODE_ERROR("331", "匹配数据库节点集群失败"),
	
	_ACCESS_DB_ERROR("333", "数据库操作失败"),
	
	_SERVER_INTERVAL_ERROR("501", "Server Interval Error"),
	
	_METHOD_NOT_FOUND("502", "Method not found"),
	
	_JSON_RPC_PROTOCAL_ERROR("503", "json-rpc protocal Error!"),
	
	_MISS_PARAM("700", "缺少参数"),
	
	_MISS_REQUIRED_PARAM("701", "required缺少参数"),
	
	_MUST_INTEGER_PARAM("702", "integer: 没有或者必须为整数 无参数"),
	
	_INTEGER_RANGE_ERROR("703","integerRange:整数范围必须在参数0和参数1之间; param0和param1必须能被转化成整数"),
	
	_DATE_FORMAT_ERROR("704", "date: 必须为日期格式, param0必须为yyyy-mm-dd"),
	
	_ENUM_PARAM_ERROR("705","enum:必须是一系列枚举值中的一个(param0中用逗号分割出来的集合)"),
	
	_MIN_LENGTH_ERROR("706","minLength: 参数最小长度不正确"),
	
	_MAX_LENGTH_ERROR("707","maxLength: 参数最大长度不正确"),
	
	_ALLWO_REG_VERIFY("708", "mask: 允许自定义正则表达式来进行校验, param0为表达式字符串"),
	
	_MUST_DOUBLE_PARAM("709", "double: 允许为空或者必须为double数 无参数"),
	
	_DOUBLE_RANGE_ERROR("710", "doubleRange:浮点范围必须在参数0和参数1之间; param0和param1必须能被转化成浮点数"),
	
	_PARAM_TYPE_MISMATCH("711","参数类型不匹配"),
	
	_VAR_VALUE_NOT_EQUAL("712", "变量值不相等"),
	
	_FORMAT_DATA_EXCEPTION("713", "格式化数据异常"),
	
	_JAVABEAN_ASSIGNMENT_ABNORMAL("714","java bean赋值异常"),
	
	_ACCESS_JAVABEAN_ATTR_ABNORMAL("715","访问java bean属性异常"),
	
	_CALL_JAVABEAN_METHOD_ABNORMAL("716","调用java bean方法异常"),
	
	_SERIALIZE_ABNORMAL("717","对象序列化异常"),
	
	_DESERIALIZATION_ABNORMAL("718", "对象反序列化异常"),
	
	_TARGET_OBJECT_NOT_FOUND("719","未找到目标对象"),
	
	_ENCODING_ABNORMAL("720","编码解码数据异常"),
	
	_ENCRYPTION_ABNORMAL("721", "加解密数据异常"),
	
	_APPID_ERROR("2200","appId[platform]未知异常"),
	
	_TOKEN_INVALID("2202","认证令牌(auth token)无效"),
	
	_DEGIST_INVAILD("2203", "认证令牌(auth token)的签名(sign)无效"),
	
	_ASC_KEY_ERROR("2204","认证令牌(auth token)的加密种子(seed)无效"),
	
	_REGISTER_ACCOUNT_FAILURE("2205","无法注册用户"),
	
	_ACCOUNT_EXISTS("2206","登录名(loginId)已存在,注册用户失败"),
	
	_REGISTER_IP_INVAILD("2207", "注册来源IP无效"),
	
	_REGISTER_AUTH_FAILURE("2208","创建用户鉴权失败,注册用户失败"),
	
	_REGISTER_ERROR("2209","创建用户信息失败,注册用户失败"),
	
	_APPID_NOT_EXISTS("2210","ppId参数不存在,注册用户失败"),
	
	_REGISTER_SOURCE_IP_ERROR("2211","注册来源IP不正确,注册用户失败"),
	
	_ACCOUNT_NOT_EXISTS("2212","您好,您的账号不存在,请重新确认,谢谢."),
	
	_ID_NOT_EXISTS("2213","当前用户ID不存在auth数据,登录失败"),
	
	_ACCOUNT_IS_CANCELED("2214","当前用户已注销,登录失败"),
	
	_ACCOUNT_LOCKED("2215","当前用户已被封停,登录失败.请与客服联系."),
	
	_ACCOUNT_NOT_ACTIVE("2216","当前用户未激活,请您先激活后登录."),
	
	_ACCOUNT_FROZEN("2217","帐号已被禁用,请联系客服."),
	
	_PASSWORD_ERROR("2218","密码错误,登录失败."),
	
	_LOGIN_IP_INVAILD("2219","登录来源IP无效"),
	
	_LOGIN_SESSION_NOT_EXISTS("2220", "登录会话不存在"),
	
	_LOGIN_SESSION_EXPIRE("2221","登录会话已过期,请重新登录"),
	
	_LOGIN_SESSION_INVAILD("2222","登录会话子已失效,请重新登录."),
	
	_NICKNAME_EXISTS("2223","昵称(nickname)已存在,修改昵称失败"),
	
	_UNABLE_UPDATE_NICKNAME("2224","无法修改昵称"),
	
	_ORIGINAL_NICKNAME_NOT_EXISTS("2225","原昵称(nickname)不存在,修改昵称失败"),
	
	_CREATE_NICKNAME_ERROR("2226","无法创建{nickname}作为新的登录名，修改昵称失败"),
	
	_UPDATE_NICKNAME_ERROR("2227","无法更新昵称{nickname}，修改昵称失败"),
	
	_BATCH_UPDATE_NICKNAME_ERROR("2228", "无法更新用户登录ID集合{nickname}，修改昵称失败"),
	
	_PROFILE_NOT_FOUND("2229", "当前用户ID不存在profile数据"),
	
	_LOGIN_IP_INVALID("2230","登录来源IP无效"),
	
	_AUTH_NOT_FOUND("2231", "当前用户ID不存在auth数据"),
	
	_ORIGINAL_PASSWORD_ERROR("2232", "旧密码不正确，修改密码失败"),
	
	_UPDATE_PASSWORD_FAILURE("2233", "更新密码失败"),
	
	_UPDATE_EMAIL_FAILURE("2261", "密码不正确，修改邮箱失败"),
	
	_UPDATE_MOBILE_FAILURE("2262","密码不正确，修改手机失败"),
	
	_CREATE_SESSION_EXCEPTION("2263", "创建用户会话异常"),
	
	_SESSION_FORCE("2264","会话已经被强制登出"),
	
	_TRADE_PASSWORD_ATYPISM("2265","取款密码验证前后不一致"),
	
	_PASSWORD_USED("2266","您最近用过此密码，请选用其他密码"),
	
	_TRADE_PASSWORD_USED("2267","您最近用过此取款密码，请选用其他密码"),
	
	_SUB_ACCOUNT_NOT_EXISTS("2268","子账号(sub-account)记录不存在"),
	
	_SUB_ACCOUNT_EXCEPTION("2269","子账号(sub-account)状态异常,不能登录"),
	
	_SN_EXCEPTION("2270","厅主账号(sn)状态异常,不能登录"),
	
	_SN_NOT_EXISTS("2271","厅主账号(sn)记录不存在"),
	
	_SN_UNIQUE_EXCEPTION("2272","厅主账号(sn)内部唯一标记数据异常"),
	
	_TRADE_PASSWORD_ERROR("2273","取款密码错误"),
	
	_CREATE_SUB_ROLE_ERROR("2274","创建影子角色失败"),
	
	_CONFIG_SUB_ROLE_ERROR("2275","给用户配置影子角色失败"),
	
	_CREATE_AGENT_FAILURE("2276","新增代理失败"),
	
	_REGISTER_IP_LIMITED("2277","此注册来源IP被限制注册，请使用其他电脑注册."),
	
	_BINDD_BANKCARD_LIMITED("2278","此银行卡号被限制绑定，请使用其他卡号进行绑定."),
	
	_CAPITAL_FLOW_EXCEPTION("2400","批量资金流水入库异常"),
	
	_REGISTER_INIT_EXCEPTION("2401","注册用户时,初始化账户异常"),
	
	_DRAW_FAILURE("2402","账户扣款不成功,更新余额失败"),
	
	_DEPOSIT_FAILURE("2403","账户充款不成功,更新余额失败"),
	
	_BATCH_DEPOSIT_ACCOUNT_EMPTY("2404","批量账户入款列表为空"),
	
	_CAPITAL_ACCOUNT_NOT_EXISTS("2405","不存在此用户账户"),
	
	_CAPITAL_FLOW_TIMEOUT("2406","批量资金流水入库操作超时"),
	
	_CAPITAL_FLOW_AMOUNT_ATYPISM("2407","批量资金流水入库操作中,前后金额不一致"),
	
	_CAPITAL_FLOW_RECORDS_AYTPISM("2408","批量资金流水入库操作中,提交记录数与操作成功记录数不一致"),
	
	_DEPOSIT_EXCEPTION("2409","资金存款申请单状态异常"),
	
	_DEPOSIT_AUTH_EXCEPTION("2410","资金存款审批更新记录异常"),
	
	_DEPOSIT_ORDER_NOT_FOUND("2411","资金存款申请记录不存在"),
	
	_CONCURRENT_BALANCE("2412","同一用户发生多个业务同时操作账户余额异常"),
	
	_DRAW_EXCEPTION("2413","资金取款申请单状态异常"),
	
	_DRAW_AUTH_EXCEPTION("2414","资金取款审批更新记录异常"),
	
	_DRAW_ORDER_NOT_FOUND("2415","资金取款申请记录不存在"),
	
	_BALANCE_NOT_ENOUGH("2416","资金取款申请,会员账户余额不足"),
	
	_AMOUNT_MUST_POSITLIVE("2417","资金存款申请金额需要为正数"),
	
	_DEPOSIT_ACCOUNT_NOT_FOUND("2418","资金存款接收账户不存在"),
	
	_ORDER_FAILURE("order-error", "订单错误"),
	;
	
	private String code;
	
	private String msg;
	
	XintBgCode(String code, String msg){
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
		for (XintBgCode code : XintBgCode.values()) {
			if (code.getCode().equalsIgnoreCase(errorCode)) {
				return code.getMsg();
			}
		}
		return msg;
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
	
	public static Integer getOkStatus(Integer orderStatus) {
		if(1==orderStatus) {
			return OrderCode._PENDING.getCode();
		}
		if(2==orderStatus || 3 == orderStatus || 4 == orderStatus) {
			return OrderCode._SUCCESS.getCode();
		}
		if(5==orderStatus || 6 == orderStatus || 7 == orderStatus) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getResult(Integer orderStatus) {
		if(2 == orderStatus) {
			return BetResultCode._WIN.getCode();
		}
		if(3 == orderStatus) {
			return BetResultCode._DRAW.getCode();
		}
		if(4 == orderStatus) {
			return BetResultCode._LOSS.getCode();
		}
		if(5==orderStatus || 6 == orderStatus || 7 == orderStatus) {
			return BetResultCode._CANCELLED.getCode();
		}
		return BetResultCode._WAIT.getCode();
	}
	
	public static Integer getLottoOkStatus(Integer orderStatus) {
		if(0==orderStatus) {
			return OrderCode._PENDING.getCode();
		}
		if(4==orderStatus || 5 == orderStatus || 6 == orderStatus) {
			return OrderCode._SUCCESS.getCode();
		}
		if(1==orderStatus || 2 == orderStatus || 3 == orderStatus) {
			return OrderCode._FAILED.getCode();
		}
		return OrderCode._INVALID.getCode();
	}
	
	public static String getLottoResult(Integer orderStatus) {
		if(0==orderStatus) {
			return BetResultCode._WAIT.getCode();
		}
		if(6==orderStatus) {
			return BetResultCode._WIN.getCode();
		}
		if(4==orderStatus) {
			return BetResultCode._LOSS.getCode();
		}
		if(5==orderStatus) {
			return BetResultCode._DRAW.getCode();
		}
		if(1==orderStatus || 2 == orderStatus || 3 == orderStatus) {
			return BetResultCode._CANCELLED.getCode();
		}
		return "";
	}
}

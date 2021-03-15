package com.esports.constant;

/**
 * 玩家类型统一编号
 * @author jacky
 * @date 2020/05/28
 */
public enum PlayerCode {

	_MEMBER("member", "会员身份")
	
	,_AGENT("agent", "普通代理")
	
	,_TOP_AGENT("top-agent", "总代")
	
	,_FIRST_FRIEND("first","一级返佣")

	,_SECOND_FRIEND("second","二级返佣")
	
	,_THIRD_FRIEND("third","三级返佣")
	;
	private String code;

	private String message;

	PlayerCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static String getRemarks(String code) {
		String remarks = "";
		if(null == code){
			return remarks;
		}
		for(PlayerCode enumType :PlayerCode.values()){
			 if(enumType.getCode().equals(code)){
				 return enumType.getMessage();
			 }
		 }
		 return remarks;
	}
}

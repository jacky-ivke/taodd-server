package com.esports.external.handler.yabo.live;

public enum DeviceTypeCode {
	
	PC(1,0, "PC"),
	MOBILE(2,2, "H5"),
	ANDROID(3,3, "Android"),
	IOS(4,4, "ios"),
	OTHER(5,5, "其它");
	
	/**
	 * 第三方平台自定义编号
	 */
	private Integer code;
	
	/**
	 * 统一语言编号
	 */
	private Integer global;
	
	/**
	 * 说明
	 */
	private String msg;
	
	DeviceTypeCode(Integer code, Integer global, String msg){
		this.code = code;
		this.global = global;
		this.msg = msg;
	}


	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getGlobal() {
		return global;
	}

	public void setGlobal(Integer global) {
		this.global = global;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static Integer getDeviceType(Integer type) {
		if(null == type) {
			return PC.getCode();
		}
		 for(DeviceTypeCode code : DeviceTypeCode.values()){
            if(code.getGlobal().equals(type)){
                return code.getCode();
            }
        }
		return PC.getCode(); 
	}
}

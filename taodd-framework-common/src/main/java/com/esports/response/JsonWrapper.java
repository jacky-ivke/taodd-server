package com.esports.response;


import lombok.Data;
import net.sf.json.JSONObject;

import java.io.Serializable;

/**
 * @ClassName: JsonWrapper
 * @Description:将数据封装返回给前端
 * @Author: jacky
 */
@Data
public class JsonWrapper implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int code;

	private boolean success;
	
	private String msg;
	
	private Object data = null;
	
	private long timestamp;
	
	private Object extParams = new JSONObject();
	
	public JsonWrapper() {
		
	}
	
	public JsonWrapper(boolean success, int code, String msg, Object data){
		this.success = success;
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}
	
	public static <T> JsonWrapper successWrapper(T pojo, int code, String msg) {
		JsonWrapper json = new JsonWrapper(true, code, msg, pojo);
		return json;
	}
	
	public static JsonWrapper successWrapper(Integer code, String msg) {
		JsonWrapper json = new JsonWrapper(true, code, msg, null);
		return json;
	}
	
	public static JsonWrapper successWrapper(Integer code) {
		JsonWrapper json = new JsonWrapper(true, code, "Request.Success", null);
		return json;
	}

	public static <T> JsonWrapper failureWrapper(T pojo, Integer code, String msg) {
		JsonWrapper json = new JsonWrapper(false, code, msg, pojo);
		return json;
	}
	
	public static JsonWrapper failureWrapper(Integer code, String msg) {
		JsonWrapper json = new JsonWrapper(false, code, msg, null);
		return json;
	}

	public static JsonWrapper failureWrapper(){
		JsonWrapper json = new JsonWrapper(false, 500, "", null);
		return json;
	}
}

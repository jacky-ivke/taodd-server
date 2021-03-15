package com.esports.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.SqlDateDeserializer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.TimestampDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unchecked")
public final class JsonUtil {
	
	
	//序列化日期格式
	public static class DateDeserializer extends StdDeserializer<Date> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
	     * 构造方法
	     *
	     */
	    public DateDeserializer() {
	        this(null);
	    }
	    
	    /**
	     * 构造方法
	     *
	     * @param vc Class
	     */
	    public DateDeserializer(Class<?> vc) {
	        super(vc);
	    }
	    
	    /**
	     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(JsonParser,
	     *      DeserializationContext)
	     */
	    @Override
	    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
	        return DatePatternUtil.getPatternDate(p.getValueAsString());
	    }
	}
	
	//反序列化日期
	public static class ObjectMapperDateFormatExtend extends DateFormat{
		
		private static final long serialVersionUID = 1L;
	    
		private final DateFormat dateFormat;
	 
	    public ObjectMapperDateFormatExtend(DateFormat dateFormat) {//构造函数传入objectmapper默认的dateformat
	        this.dateFormat = dateFormat;
	    }
	    //序列化时会执行这个方法
	    @Override
	    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
	        return dateFormat.format(date,toAppendTo,fieldPosition);
	    }
	    
	    @Override
	    public Date parse(String source, ParsePosition pos) {
	        Date date;
	        try {
	            date = DatePatternUtil.getPatternDate(source);
	        } catch (Exception e) {
	            date = dateFormat.parse(source, pos);
	        }
	        return date;
	    }
	    @Override
	    public Object clone() {
	        DateFormat dateFormat = (DateFormat) this.dateFormat.clone();
	        return new ObjectMapperDateFormatExtend(dateFormat);
	    }
		
	}
	
	

    private JsonUtil() {
    }

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        //序列化的时候序列对象的所有属性
        // 允许没有引号的字段名
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号字段名
//        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 自动给字段名加上引号
//        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        //序列化的时候序列对象的所有属性
        mapper.setSerializationInclusion(Include.ALWAYS);
        //反序列化的时候如果多了其他属性,不抛出异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //如果是空对象的时候,不抛异常
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new DateDeserializer());
        module.addDeserializer(java.sql.Date.class, new SqlDateDeserializer());
        module.addDeserializer(java.sql.Timestamp.class, new TimestampDeserializer());
        mapper.registerModule(module);
    }

    public static String object2String(Object object) {
        if(ObjectUtils.isEmpty(object)){
            return null;
        }
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (Exception e) {
            log.error("将 object 转换为 json 字符串时发生异常", e);
            e.printStackTrace();
            return null;
        }
        return writer.toString();
    }

    public static JSONObject object2Json(Object object){
        if(ObjectUtils.isEmpty(object)){
            return null;
        }
        String str = JsonUtil.object2String(object);
        JSONObject json = JSONObject.fromObject(str);
        return json;
    }

    public static JSONArray object2JsonArr(Object object){
        if(ObjectUtils.isEmpty(object)){
            return null;
        }
        String str = JsonUtil.object2String(object);
        JSONArray arr = JSONArray.fromObject(str);
        return arr;
    }

    public static String object2String(Object object, TypeReference<?> ref) {
        if(ObjectUtils.isEmpty(object)){
            return null;
        }
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return writer.toString();
    }


    public static String map2String(Map<?, ?> map) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return writer.toString();
    }


    public static SortedMap<String, Object> string2Map(String content) {
        if (content.equals("[]") || content.equals("{}")) {
            return new TreeMap<String, Object>();
        }
        JavaType type = typeFactory.constructMapType(TreeMap.class, String.class, Object.class);
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
        	FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为Map时出现异常", content);
            throw new RuntimeException(message.getMessage(), e);
        }
    }
    
    public static SortedMap<String, Object> object2Map(Object object){
    	if(null == object) {
    		return new TreeMap<String, Object>();
    	}
    	String content = JsonUtil.object2String(object);
    	SortedMap<String, Object> map = JsonUtil.string2Map(content);
    	return map;
    }

    public static Map<String, Object> string2Map(String content, TypeReference<?> ref) {
        try {
            return (Map<String, Object>) mapper.readValue(content, ref);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为Map时出现异常", content);
            throw new RuntimeException(message.getMessage(), e);
        }
    }


    public static <T> T[] string2Array(String content, Class<T> clz) {
        JavaType type = mapper.getTypeFactory().constructArrayType(clz);
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为数组时出现异常", content, e);
            log.error(message.getMessage(), e);
            throw new RuntimeException(message.getMessage(), e);
        }
    }
    
    public static <T> List<T> string2List(String content, Class<T> clz) {
        JavaType type = mapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, clz);
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为数组时出现异常", content, e);
            log.error(message.getMessage(), e);
            throw new RuntimeException(message.getMessage(), e);
        }
    }

    public static <T> T map2Object(Map<String,Object> map, Class<T> clz) {
        T bean = null;
    	try {
        	String str = map2String(map);
        	bean = string2Object(str, clz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    	return bean;
    }
    

    public static <T> T string2Object(String content, Class<T> clz) {
        JavaType type = typeFactory.constructType(clz);
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为对象[{}]时出现异常",
                    new Object[]{content, clz.getSimpleName(), e});
            throw new RuntimeException(message.getMessage(), e);
        }
    }


    public static <C extends Collection<E>, E> C string2Collection(String content, Class<C> collectionType,
                                                                   Class<E> elementType) {
        try {
            JavaType type = typeFactory.constructCollectionType(collectionType, elementType);
            return mapper.readValue(content, type);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为集合[{}]时出现异常", new Object[]{content,
                    collectionType.getSimpleName(), e});
            throw new RuntimeException(message.getMessage(), e);
        }
    }

	public static <K, V> Map<K, V> string2Map(String content, Class<K> keyType, Class<V> valueType) {
        JavaType type = typeFactory.constructMapType(HashMap.class, keyType, valueType);
        try {
            return mapper.readValue(content, type);
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.format("将字符串[{}]转换为Map时出现异常", content);
            throw new RuntimeException(message.getMessage(), e);
        }
    }
}

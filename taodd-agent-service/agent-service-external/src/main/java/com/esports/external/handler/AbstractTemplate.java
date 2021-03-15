package com.esports.external.handler;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 统一外部应用调用接口规范(模板方法模式)
 *
 * @author jacky
 */
public abstract class AbstractTemplate {

    public String utc2Local(String utcTime, String utcTimePatten, String localTimePatten, String utcZone) {
        String localTime = "";
        try {
            SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
            utcFormater.setTimeZone(TimeZone.getTimeZone(utcZone));
            Date gpsUTCDate = utcFormater.parse(utcTime);
            SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
            localFormater.setTimeZone(TimeZone.getDefault());
            localTime = localFormater.format(gpsUTCDate.getTime());
        } catch (Exception e) {
        }
        return localTime;
    }

    public String local2Utc(String localTime, String utcTimePatten, String localTimePatten, String utcZone) {
        String utcTime = "";
        try {
            SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
            localFormater.setTimeZone(TimeZone.getDefault());
            Date gpsLocalDate = localFormater.parse(localTime);
            SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
            utcFormater.setTimeZone(TimeZone.getTimeZone(utcZone));
            utcTime = utcFormater.format(gpsLocalDate);
        } catch (Exception e) {
        }
        return utcTime;
    }

    public JSONArray json2JsonArr(String data) {
        JSONArray arr = new JSONArray();
        if (StringUtils.isEmpty(data)) {
            return arr;
        }
        if (isJsonObject(data)) {
            arr.add(JSONObject.fromObject(data));
        } else if (isJsonArry(data)) {
            arr = JSONArray.fromObject(data);
        }
        return arr;
    }

    public boolean isJsonObject(String data) {
        boolean success = false;
        if (StringUtils.isEmpty(data)) {
            return success;
        }
        try {
            JSONObject.fromObject(data);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    public boolean isJsonArry(String data) {
        boolean success = false;
        if (StringUtils.isEmpty(data)) {
            return success;
        }
        try {
            JSONArray.fromObject(data);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    public static BigDecimal getActualAmount(BigDecimal amount, RespResultDTO dto) {
        BigDecimal actualAmount = null != dto && null != dto.getData() ? new BigDecimal(dto.getData().toString()) : amount;
        actualAmount = actualAmount.compareTo(BigDecimal.ZERO) == 0 ? amount : actualAmount;
        actualAmount = formatAmount(actualAmount);
        return actualAmount;
    }

    public static BigDecimal cent2Yuan(BigDecimal cent, BigDecimal multiple) {
        BigDecimal yuan = BigDecimal.ZERO;
        if (null != cent) {
            yuan = cent.divide(multiple).setScale(2, BigDecimal.ROUND_DOWN);
        }
        return yuan;
    }

    public static BigDecimal yuan2Cent(BigDecimal yuan, BigDecimal multiple) {
        BigDecimal cent = BigDecimal.ZERO;
        if (null != yuan) {
            cent = yuan.multiply(multiple).setScale(2, BigDecimal.ROUND_DOWN);
        }
        return cent;
    }

    public RespResultDTO buildBalanceResult(RespResultDTO dto, String errorCode) {
        RespResultDTO result = new RespResultDTO();
        if (null == dto || null == dto.getData() || !dto.getSuccess()) {
            result.setCode(errorCode);
            result.setSuccess(Boolean.FALSE);
            result.setData(BigDecimal.ZERO);
            return result;
        }
        BigDecimal amount = new BigDecimal(dto.getData().toString());
        result.setCode(String.valueOf(HttpStatus.SC_OK));
        result.setSuccess(Boolean.TRUE);
        result.setData(formatAmount(amount));
        return result;
    }

    public static BigDecimal formatAmount(BigDecimal amount) {
        amount = null == amount ? BigDecimal.ZERO : amount;
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN).abs();
        return amount;
    }

    public RespResultDTO deposit(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {

        return null;
    }

    public RespResultDTO draw(String loginName, Integer lang, String orderNo, BigDecimal amount, Integer deviceType, String ip, Long timestamp) {

        return null;
    }

    public RespResultDTO searchOrder(String loginName, String orderNo, Integer lang, Long timestamp) {


        return null;
    }

    public RespResultDTO balance(String loginName, String ip) {

        return null;
    }

    public RespResultDTO playGame(String loginName, Integer deviceType, String memberIp, Integer lang, String gameCode, String gameType, BigDecimal amount, String transferNo, Long timestamp) {

        return null;
    }

    public RespResultDTO history(String startTime, String endTime, Long pageIndex) {

        return null;
    }

    public void syncGames() {

    }

    public void syncData() {

    }
}

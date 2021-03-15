package com.esports.utils;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

	/**
	 * 得到某年某周的第一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getFirstDayOfWeek(int year, int week) {
		week = week - 1;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DATE, 1);
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.DATE, week * 7);
		return getFirstDayOfWeek(cal.getTime());
	}

	/**
	 * 得到某年某周的最后一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getLastDayOfWeek(int year, int week) {
		week = week - 1;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DATE, 1);
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.DATE, week * 7);
		return getLastDayOfWeek(cal.getTime());
	}

	/**
	 * 取得当前日期所在周的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Sunday
		return calendar.getTime();
	}

	public static Timestamp getBeginDayOfWeek() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == 1) {
			dayofweek += 7;
		}
		cal.add(Calendar.DATE, 2 - dayofweek);
		return getDayStartTime(cal.getTime());
	}

	public static Timestamp getDayStartTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// 获取某个日期的结束时间
	public static Timestamp getDayEndTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// 获取本周的结束时间
	public static Timestamp getEndDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getBeginDayOfWeek());
		cal.add(Calendar.DAY_OF_WEEK, 6);
		Date weekEndSta = cal.getTime();
		return getDayEndTime(weekEndSta);
	}

	/**
	 * 取得当前日期所在周的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6); // Saturday
		return calendar.getTime();
	}

	/**
	 * 取得当前日期所在周的前一周最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfLastWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getLastDayOfWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR) - 1);
	}

	/**
	 * 返回指定日期的月的第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的月的第一天的00:00:00
	 * 
	 * @return
	 */
	public static Date getFirstDayTimeOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 返回指定年月的月的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getFirstDayOfMonth(Integer year, Integer month) {
		Calendar calendar = Calendar.getInstance();
		if (year == null) {
			year = calendar.get(Calendar.YEAR);
		}
		if (month == null) {
			month = calendar.get(Calendar.MONTH);
		}
		calendar.set(year, month, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的月的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 返回指定年月的月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(Integer year, Integer month) {
		Calendar calendar = Calendar.getInstance();
		if (year == null) {
			year = calendar.get(Calendar.YEAR);
		}
		if (month == null) {
			month = calendar.get(Calendar.MONTH);
		}
		calendar.set(year, month, 1);
		calendar.roll(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		return calendar.getTime();
	}

	/**
	 * 返回指定日期的上个月的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfLastMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的季的第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfQuarter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getFirstDayOfQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
	}

	/**
	 * 返回当年的第一天
	 * 
	 * @return
	 */
	public static Date getCurrYearFirst() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearFirst(currentYear);
	}

	public static Date getYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 返回指定年季的季的第一天
	 * 
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getFirstDayOfQuarter(Integer year, Integer quarter) {
		Calendar calendar = Calendar.getInstance();
		Integer month = new Integer(0);
		if (quarter == 1) {
			month = 1 - 1;
		} else if (quarter == 2) {
			month = 4 - 1;
		} else if (quarter == 3) {
			month = 7 - 1;
		} else if (quarter == 4) {
			month = 10 - 1;
		} else {
			month = calendar.get(Calendar.MONTH);
		}
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return getFirstDayOfMonth(year, month);
	}

	/**
	 * 获取指定日期的零点时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayZero(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的季的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfQuarter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getLastDayOfQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
	}

	/**
	 * 返回指定年季的季的最后一天
	 * 
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getLastDayOfQuarter(Integer year, Integer quarter) {
		Calendar calendar = Calendar.getInstance();
		Integer month = new Integer(0);
		if (quarter == 1) {
			month = 3 - 1;
		} else if (quarter == 2) {
			month = 6 - 1;
		} else if (quarter == 3) {
			month = 9 - 1;
		} else if (quarter == 4) {
			month = 12 - 1;
		} else {
			month = calendar.get(Calendar.MONTH);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return getLastDayOfMonth(year, month);
	}

	/**
	 * 返回指定日期的上一季的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfLastQuarter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getLastDayOfLastQuarter(calendar.get(Calendar.YEAR), getQuarterOfYear(date));
	}

	/**
	 * 返回指定年季的上一季的最后一天
	 * 
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getLastDayOfLastQuarter(Integer year, Integer quarter) {
		Calendar calendar = Calendar.getInstance();
		Integer month = new Integer(0);
		if (quarter == 1) {
			month = 12 - 1;
		} else if (quarter == 2) {
			month = 3 - 1;
		} else if (quarter == 3) {
			month = 6 - 1;
		} else if (quarter == 4) {
			month = 9 - 1;
		} else {
			month = calendar.get(Calendar.MONTH);
		}
		return getLastDayOfMonth(year, month);
	}

	/**
	 * 返回指定日期的季度
	 * 
	 * @param date
	 * @return
	 */
	public static int getQuarterOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) / 3 + 1;
	}

	public static final String Pattern_Date = "yyyy-MM-dd HH:mm:ss";
	public static final String Pattern_yyyy_MM_dd = "yyyy-MM-dd";
	public static final String Pattern_yyyy_MM = "yyyy-MM";
	public static final String Pattern_yyyy = "yyyy";

	public static String dateFormartString(Date date, String pattern) {
		SimpleDateFormat df24 = new SimpleDateFormat(pattern);
		return df24.format(date);
	}

	public static int getDayCount(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return (int) diffDays + 1;
	}

	public static Date stringFormatDate(String date, String pattern) {
		SimpleDateFormat df24 = new SimpleDateFormat(pattern);
		try {
			return df24.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date stringFormatDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat(Pattern_yyyy_MM_dd);
		try {
			return df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("static-access")
	public static List<Date> getWeekDays(int year, int month, int weekIndx) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);

		while (calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) != weekIndx) {
			if (calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) > weekIndx) {
				calendar.add(calendar.WEEK_OF_MONTH, -1);
			} else {
				calendar.add(calendar.WEEK_OF_MONTH, 1);
			}
		}

		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, -1);
		}

		List<Date> dayList = new ArrayList<Date>();
		for (int i = 0; i < 7; i++) {
			dayList.add(calendar.getTime());
			calendar.add(Calendar.DATE, 1);
		}
		return dayList;
	}

	/**
	 * 在原日期的基础上增加天数
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getDayOfSubDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, day);
		Date newDate = c.getTime();
		return newDate;
	}

	/**
	 * 在原日期的基础上增加天数并获得00:00:00的时间
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getDayFirstTimeOfSubDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, day);
		Date newDate = c.getTime();
		Date dayFirstTime = getDayFirstTime(newDate);
		return dayFirstTime;
	}

	public static Date getDayFirstTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getDayLastTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static Date getDayFirstTime(String time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getDayLastTime(String time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
	
    public static String getDayStartTime(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        try {
            Date date = stringFormatDate(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return DateUtils.dateFormartString(calendar.getTime(), Pattern_Date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDayEndTime(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        try {
            Date date = stringFormatDate(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return DateUtils.dateFormartString(calendar.getTime(), Pattern_Date);
        } catch (Exception e) {
            return "";
        }
    }


	/**
	 * 获取今天剩余时间(秒)
	 */
	public static long getDaySurplusTime() {
		// 今天剩余的时间(秒)
		Date date = DateUtils.getDayLastTime(new Date(System.currentTimeMillis()));
		Long time = (date.getTime() - System.currentTimeMillis()) / 1000;
		return time;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	public static String getYear() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.Pattern_yyyy);
		return sdf.format(date);
	}

	public static String getMonth() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.Pattern_yyyy_MM);
		return sdf.format(date);
	}

	public static String formatMonth(String month){
		String formatMont = "";
		if(StringUtils.isEmpty(month)){
			return formatMont;
		}
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
			formatMont = df.format(df.parse(month));
		}catch (Exception ex){
			formatMont = month;
		}
		return formatMont;
	}

	public static Integer getQuarter() {
		Calendar cal = Calendar.getInstance();
		int m = cal.get(Calendar.MONTH);
		int quarter = 0;
		if (m >= 1 && m == 3) {
			quarter = 1;
		} else if (m >= 4 && m <= 6) {
			quarter = 2;
		} else if (m >= 7 && m <= 9) {
			quarter = 3;
		} else {
			quarter = 4;
		}
		return quarter;
	}

	/**
	 * 获取当前时间减去 加上days后的时间
	 * 
	 * @param days
	 * @return
	 */
	public static String getSubDate(int days) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date end = DateUtils.getDayOfSubDay(date, days);
		return sdf.format(end);
	}

	/**
	 * befor是日期格式字符串 end是时间戳 秒单位 中间的时间在前后时间中
	 */
	public static Boolean compare(String befor, String end, String after) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Long aLong1 = null;
		Long aLong2 = Long.valueOf(end);
		Date dayLastTime = DateUtils.getDayLastTime(after);
		try {
			aLong1 = Long.valueOf(sdf1.parse(befor).getTime() / 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long aLong3 = dayLastTime.getTime() / 1000;
		return aLong1 <= aLong2 && aLong2 <= aLong3;
	}

	/**
	 * 比较两个日期是否超过一天 日历上的一天 不是24小时的一天
	 */
	public static Boolean compare(String one, String two) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date aLong1 = null;
		Date aLong2 = null;
		try {
			aLong1 = sdf.parse(one);
			aLong2 = sdf.parse(two);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(aLong1);
		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(aLong2);
		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
		return day2 - day1 >= 1;
	}

	public static String getPreMonth() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -1);
		Date preMonth = cal.getTime();
		String format = sdf.format(preMonth);
		return format;
	}

	public static String getFirstDayOfCurrentMonth() {
		String day = "";
		try {
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			day = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_Date);
		} catch (Exception e) {
			day = DateUtils.getDate();
			e.printStackTrace();
		}
		return day;
	}

	public static String getLastDayOfCurrentMonth() {
		String day = "";
		try {
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			day = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_Date);
		} catch (Exception e) {
			day = DateUtils.getDate();
			e.printStackTrace();
		}
		return day;
	}

	public static String getBeforeThirtyDaysTime() {
		String day = "";
		try {
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.add(Calendar.DATE, -30);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0, 0, 0);
			day = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_Date);
		} catch (Exception e) {
			day = DateUtils.getDate();
			e.printStackTrace();
		}
		return day;
	}

	public static String getCurrentTime() {
		String day = "";
		try {
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			day = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_Date);
		} catch (Exception e) {
			day = DateUtils.getDate();
			e.printStackTrace();
		}
		return day;
	}
	
	public static String getCurrentTime(String date) {
		String day = "";
		Date now = new Date();
		Date cdate = StringUtils.isEmpty(date)? now : DateUtils.stringFormatDate(date);
		if(cdate.after(now)) {
			cdate = now;
		}
		day = DateUtils.dateFormartString(cdate, DateUtils.Pattern_Date);
		return day;
	}
	
	/**
	 * 输入日期字符串比如2017-03，返回当月第一天的Date
	 * 
	 * @param month
	 * @return
	 */
	public static Date getMinDateMonth(String month) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date nowDate = sdf.parse(month);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			return calendar.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date getMaxDateMonth(String month) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date nowDate = sdf.parse(month);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			return calendar.getTime();
			// return DateUtils.dateFormartString(calendar.getTime(),
			// DateUtils.Pattern_yyyy_MM);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getSectionDateMonth(String month) {
		List<String> list = new ArrayList<String>();
		try {
			month = StringUtils.isEmpty(month) ? DateUtils.getMonth() : month;
			Date minDate = DateUtils.getMinDateMonth(month);
			Date maxDate = DateUtils.getMaxDateMonth(month);
			long start = minDate.getTime();
			long end = maxDate.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(minDate);
			while (end >= start) {
				String date = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_yyyy_MM_dd);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				start = calendar.getTimeInMillis();
				list.add(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Timestamp getYesterDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, -24);
		Date date = getDayLastTime(calendar.getTime());
		return new Timestamp(date.getTime());
	}

	public static Timestamp getLastDayOfLastMonth() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date date = getDayLastTime(calendar.getTime());
		return new Timestamp(date.getTime());
	}

	public static Timestamp utcToLocal(String utcTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date utcDate = null;
		Timestamp locatlDate = null;
		try {
			utcDate = formatter.parse(utcTime);
			Date date = DateUtils.stringFormatDate(sdf2.format(utcDate), DateUtils.Pattern_Date);
			locatlDate = new Timestamp(date.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return locatlDate;
	}

	public static String localToUtc(String localTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String utcDate = null;
		Date locatlDate = null;
		locatlDate = DateUtils.stringFormatDate(localTime, DateUtils.Pattern_Date);
		utcDate = formatter.format(locatlDate);
		return utcDate;
	}

	public static String getAfterThirtyDaysTime() {
		String day = "";
		try {
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.add(Calendar.DATE, 30);
			day = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_Date);
		} catch (Exception e) {
			day = DateUtils.getDate();
			e.printStackTrace();
		}
		return day;
	}
	
    public static List<String> getSectionDate(String startTime, String endTime) {
        List<String> list = new ArrayList<String>();
        try {
            Date minDate = DateUtils.stringFormatDate(startTime);
            Date maxDate = DateUtils.stringFormatDate(endTime);
            long start = minDate.getTime();
            long end = maxDate.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(minDate);
            while (end >= start) {
                String date = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_yyyy_MM_dd);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                start = calendar.getTimeInMillis();
                list.add(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
	public static String getThisWeekMonday() {
		String day = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            day = DateUtils.dateFormartString(calendar.getTime(), DateUtils.Pattern_Date);
            day = DateUtils.getDayStartTime(day);
        } catch (Exception e) {
            day = DateUtils.getDate();
            e.printStackTrace();
        }
        return day;
	}
	
	 public static String getFirstOfCurrentDay() {
	        Date now = new Date();
	        Timestamp time = getDayStartTime(now);
	        return DateUtils.dateFormartString(new Date(time.getTime()), Pattern_Date);
	    }

	    public static String getLastOfCurrentDay() {
	        Date now = new Date();
	        Timestamp time = getDayEndTime(now);
	        return DateUtils.dateFormartString(new Date(time.getTime()), Pattern_Date);
	    }

}

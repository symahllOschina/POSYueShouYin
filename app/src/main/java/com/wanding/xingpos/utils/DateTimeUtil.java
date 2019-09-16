package com.wanding.xingpos.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 注意SimpleDateFormat是线程非安全的，在高并发情况下会出现问题
 * 可以使用使用org.apache.commons.lang.time.FastDateFormat 和 org.apache.commons.lang.time.DateFormatUtils
 */
@SuppressWarnings({"ALL", "AlibabaAvoidNewDateGetTime"})
@SuppressLint("SimpleDateFormat")
public class DateTimeUtil {


	
	/** 获取系统时间 */
	@SuppressLint("SimpleDateFormat")
	public static String getSystemTime(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);  
	}
	
	/*
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String time) throws ParseException {
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	    Date date = simpleDateFormat.parse(time);
	    long ts = date.getTime();
	    return String.valueOf(ts);
	}
	
	/*
	 * 将时间戳转换为时间
	 */
	public static String stampToDate(long timeMillis){
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date = new Date(timeMillis);
	    return simpleDateFormat.format(date);
	}
	
	/*
	 * 将时间戳转换为时间
	 * format：要转换的时间格式：例如"yyyy-MM-dd HH:mm:ss" MM月dd日 HH:mm:ss
	 */
	public static String stampToFormatDate(long timeMillis, String format){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = new Date(timeMillis);
		return simpleDateFormat.format(date);
	}

	/**
     * 将时间字符串(不是时间戳)转化为日期格式字符串
     * @param timeStr 例如：20160325160000
     * @return String 例如：2016-03-25 16:00:00
	 *
     */
    public static String timeStrToDateStr(String timeStr){
        if (null == timeStr) {
            return null;
        }
        String dateStr = null;
        SimpleDateFormat sdf_input = new SimpleDateFormat("yyyyMMddHHmmss");//输入格式
        SimpleDateFormat sdf_target =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //转化成为的目标格式
        try {
            //将20160325160000转化为Fri Mar 25 16:00:00 CST 2016,再转化为2016-03-25 16:00:00
            dateStr = sdf_target.format(sdf_input.parse(timeStr));
        } catch (Exception e) {
        }
        return dateStr;
    }

    /**
     * 将时间字符串(不是时间戳)转化为指定格式的日期格式字符串
     * timeStr：要转换的字符串
     * format：要转换的格式
     * @param timeStr 例如：20160325160000
     * @return String 例如：2016-03-25 16:00:00
     */
    public static String timeStrToFormatDateStr(String timeStr, String format){
    	if (null == timeStr) {
    		return null;
    	}
    	String dateStr = null;
    	SimpleDateFormat sdf_input = new SimpleDateFormat("yyyyMMddHHmmss");//输入格式
    	SimpleDateFormat sdf_target =new SimpleDateFormat(format); //转化成为的目标格式
    	try {
    		//将20160325160000转化为Fri Mar 25 16:00:00 CST 2016,再转化为2016-03-25 16:00:00
    		dateStr = sdf_target.format(sdf_input.parse(timeStr));
    	} catch (Exception e) {
    	}
    	return dateStr;
    }

    public static String getDateTimeFormatStr(String timeStr) throws ParseException {
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);

        return DateFormatUtils.format(date,"yyyy-MM-dd HH:mm:ss");
    }
	
	/**
     * 根据日期获得星期
     * @param date
     * @return
     */
	public static String getWeekOfDate(Date date) {
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}
	
	/**
	 * 根据指定日期获取N天后的日期
	 * dateStr : 指定返回的日期String
	 */
	@SuppressWarnings("AlibabaAvoidNewDateGetTime")
	public static String getDateStr(int dayNum){
		String dateStr="";
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");  //字符串转换
		Calendar c = Calendar.getInstance();
	     //new Date().getTime();这个是获得当前电脑的时间，你也可以换成一个随意的时间
		 c.setTimeInMillis(System.currentTimeMillis());
		 c.add(Calendar.DATE, dayNum);//天后的日期
		 Date date= new Date(c.getTimeInMillis()); //将c转换成Date
		 dateStr=formatDate.format(date);
		return dateStr;
	}
	
	public static String getDateStr(int dayNum, String format){
		String dateStr="";
		SimpleDateFormat formatDate = new SimpleDateFormat(format);  //字符串转换
		Calendar c = Calendar.getInstance();
		//new Date().getTime();这个是获得当前电脑的时间，你也可以换成一个随意的时间
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.DATE, dayNum);//天后的日期
		Date date= new Date(c.getTimeInMillis()); //将c转换成Date
		dateStr=formatDate.format(date);
		return dateStr;
	}
	
	/**
	 * 根据指定日期获取N月后的日期
	 * 例如： 如获取前3个月日期则传-3即可；如果后3个月则传3
	 * dateStr : 指定返回的日期String
	 */
	public static String getAMonthDateStr(int dayNum, String format){
		String dateStr = "";
		SimpleDateFormat sdf=new SimpleDateFormat(format); //设置时间格式
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, dayNum);//天后的日期
		Date date= new Date(calendar.getTimeInMillis()); //将c转换成Date
		dateStr=sdf.format(date);

//		Date dNow = new Date();   //当前时间
//		Date dBefore = new Date();
//		calendar.setTime(dNow);//把当前时间赋给日历
//		calendar.add(calendar.MONTH, dayNum);  //设置为前3月
//		dBefore = calendar.getTime();   //得到前3月的时间
//		String defaultStartDate = sdf.format(dBefore);    //格式化前3月的时间
//		String defaultEndDate = sdf.format(dNow); //格式化当前时间

		return dateStr;
	}

	/**
	 * 根据指定日期获取N年后的日期
	 * 例如： 如获取前3个月日期则传-3即可；如果后3个月则传3
	 * dateStr : 指定返回的日期String
	 */
	public static String getYearDateStr(int dayNum, String format){
		String dateStr = "";
		SimpleDateFormat sdf=new SimpleDateFormat(format); //设置时间格式
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.YEAR, dayNum);//年后的日期
		Date date= new Date(calendar.getTimeInMillis()); //将c转换成Date
		dateStr=sdf.format(date);

		return dateStr;
	}

	/**
	 * 日期比较大小（注意格式：yyyy-MM-dd HH:mm）
	 * startDate: "1995-11-12 15:21",
	 * endDate: "1999-12-11 09:59"
	 * startDate > endDate return 1
	 * startDate < endDate return -1
	 */
	public static int compareDate(String startDate, String endDate){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date dt1 = df.parse(startDate);
			Date dt2 = df.parse(endDate);
			Log.e("起始时间：", dt1.getTime()+"");
			Log.e("结束时间：", dt2.getTime()+"");
			if (dt1.getTime() > dt2.getTime()) {
				Log.e("日期比较结果","dt1在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				Log.e("日期比较结果","dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 判断系统日期时间是否在指定日期时间段内
	 * beginHour：起始时间小时数  如早上05点 beginHour=05
	 * endHour ： 结束日期时间段   如晚上20点 endHour=20
	 */
	public static boolean isTimeSlot(int beginHour,int endHour){
		boolean isTime=false;
		//起始日期时间段：
		Date beginDate =new Date();
		beginDate.setHours(beginHour);
		beginDate.setSeconds(0);
		beginDate.setMinutes(0);
		 //结束日期时间段
		Date endDate =new Date();
		endDate.setHours(endHour);
		endDate.setSeconds(0);
		endDate.setMinutes(0);
		 //系统时间
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println(fmt.format(beginDate));
		Date now =new Date();
		//系统时间在起始时间之后并且在结束时间之前
		if(now.after(beginDate) && now.before(endDate)){
			isTime = true;
		}
		return isTime;
	}
	
	/**
	 * 日期统一格式
	 */
	private final static SimpleDateFormat FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取下一秒的时间
	 * 
	 * @param currentDate
	 * @return
	 */
	public static String getDateAddOneSecond(String currentDate) {

		String nextSecondDate = "";

		if (currentDate != null && !"".equals(currentDate)) {

			try {
				Date date = FORMAT.parse(currentDate); // 将当前时间格式化
				// System.out.println("front:" + format.format(date)); //
				// 显示输入的日期
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.SECOND, 1); // 当前时间加1秒
				date = cal.getTime();
				// System.out.println("after:" + format.format(date));
				nextSecondDate = FORMAT.format(date); // 加一秒后的时间
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nextSecondDate;
	}

	/**
	 * 获取剩余时间 几天几时几分几秒
	 * @param systemTime：系统时间（既起始时间）
	 * @param startTime：目标起始时间
	 * @param endTime：目标结束时间
	 * @return
	 */
	public static String getRemainTime(String systemTime, String startTime, String endTime) {

		String remainTime = ""; // 剩余时间

		long dayMsec = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long hourMsec = 1000 * 60 * 60;// 一小时的毫秒数
		long minuteMsec = 1000 * 60;// 一分钟的毫秒数
		long secondMsec = 1000;// 一秒钟的毫秒数
		long diffMsec; // 毫秒差

		if (systemTime != null && !"".equals(systemTime) && startTime != null
				&& !"".equals(startTime)) {
			try {
				// 获得两个时间的毫秒时间差异
				diffMsec = FORMAT.parse(startTime).getTime() - FORMAT.parse(systemTime).getTime();
				if(diffMsec > 0){
					/*判断结束时间是否大于开始时间*/
					long diffDay = diffMsec / dayMsec;// 计算差多少天
					long diffHour = diffMsec % dayMsec / hourMsec;// 计算差多少小时
					long diffMin = diffMsec % dayMsec % hourMsec / minuteMsec;// 计算差多少分钟
					long diffSec = diffMsec % dayMsec % dayMsec % minuteMsec / secondMsec;// 计算差多少秒//输出结果
					String returnDayStr="";
					if(diffDay>0){
						returnDayStr=diffDay + "天";
					}
//					remainTime = returnDayStr + diffHour + "时" + diffMin + "分"+ diffSec + "秒";
					remainTime = returnDayStr + diffHour + "时" + diffMin + "分";
				}
				else{
					if(FORMAT.parse(systemTime).getTime()>=FORMAT.parse(startTime).getTime()){
						//如果系统时间 >= 目标起始时间，赋值为1：表示开始学车
						remainTime="1";
					}else if(FORMAT.parse(systemTime).getTime()>=FORMAT.parse(endTime).getTime()){
						//如果系统时间 >= 目标结束时间，赋值为2：表示学车结束
						remainTime="2";
					}
					
					
					
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return remainTime;
	}
	/**
	 * 格式化日期格式
	 * 
	 * @param dateTimeString
	 * @return
	 */
	public static String formatDateType(String dateTimeString) {

		String formatAfterDateTimeString = "";
		// System.out.println(dateTimeString);

		if (dateTimeString != null && !"".equals(dateTimeString)) {
			/* 判断字符串是否有值 */
			formatAfterDateTimeString = dateTimeString;

			if (formatAfterDateTimeString.contains("/")) {
				/* 判断日期中是否包含'/' */
				formatAfterDateTimeString = formatAfterDateTimeString.replace(
						"/", "-");
			}

			if ((formatAfterDateTimeString.lastIndexOf("-") - formatAfterDateTimeString
					.indexOf("-")) == 2) {
				/* 判断月份格式是否是MM格式 */
				String frontSubString = formatAfterDateTimeString.substring(0,
						formatAfterDateTimeString.indexOf("-") + 1);
				String afterSubString = "0" + formatAfterDateTimeString.substring(
						formatAfterDateTimeString.indexOf("-") + 1,
						formatAfterDateTimeString.length());
				
				formatAfterDateTimeString = frontSubString + afterSubString; //拼接字符串
			}
		}
		return formatAfterDateTimeString;
	}
}

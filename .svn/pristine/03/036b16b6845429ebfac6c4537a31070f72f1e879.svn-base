package com.wanding.xingpos.summary.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SummaryDateUtil {

	
	/**
     * 根据指定日期获取时间戳
     */
	public static String getStartTimeStampTo(String dateStr){
		String timeStr = "000000";
		String sysDateStr = "";
		String year = dateStr.split("-")[0];
        String month = dateStr.split("-")[1];
        String day = dateStr.split("-")[2];
        sysDateStr = year+month+day;
        
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = null;
            date = simpleDateFormat.parse(sysDateStr+timeStr);
            long ts = date.getTime();
            String stampStr = String.valueOf(ts);
            Log.e("生成的时间戳",stampStr);
            return stampStr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
	
	/**
	 * 根据指定日期获取时间戳
	 */
	public static String getEndTimeStampTo(String dateStr){
		String timeStr = "235959";
		String sysDateStr = "";
		String year = dateStr.split("-")[0];
		String month = dateStr.split("-")[1];
		String day = dateStr.split("-")[2];
		sysDateStr = year+month+day;
		
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = null;
			date = simpleDateFormat.parse(sysDateStr+timeStr);
			long ts = date.getTime();
			String stampStr = String.valueOf(ts);
			Log.e("生成的时间戳",stampStr);
			return stampStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
}

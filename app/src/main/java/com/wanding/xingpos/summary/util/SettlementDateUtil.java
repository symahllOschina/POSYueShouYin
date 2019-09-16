package com.wanding.xingpos.summary.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SettlementDateUtil {

	
	/**
     * 根据指定日期获取时间戳
     */
	public static String getStartTimeStampTo(String dateTimeStr){
		String sysDateStr = "";
		
		String dateStr = dateTimeStr.split(" ")[0];
        String timeStr = dateTimeStr.split(" ")[1];
		
		String year = dateStr.split("-")[0];
        String month = dateStr.split("-")[1];
        String day = dateStr.split("-")[2];
        String hour = timeStr.split(":")[0];
        String minute = timeStr.split(":")[1];
        sysDateStr = year+month+day+hour+minute;
        
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            Date date = null;
            date = simpleDateFormat.parse(sysDateStr);
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
	@SuppressLint("SimpleDateFormat")
	public static String getEndTimeStampTo(String dateTimeStr){
		String sysDateStr = "";
		
		String dateStr = dateTimeStr.split(" ")[0];
        String timeStr = dateTimeStr.split(" ")[1];
		
		String year = dateStr.split("-")[0];
        String month = dateStr.split("-")[1];
        String day = dateStr.split("-")[2];
        String hour = timeStr.split(":")[0];
        String minute = timeStr.split(":")[1];
        sysDateStr = year+month+day+hour+minute;
        
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            Date date = null;
            date = simpleDateFormat.parse(sysDateStr);
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

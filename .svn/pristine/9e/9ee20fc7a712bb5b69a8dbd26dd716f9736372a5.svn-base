package com.wanding.xingpos.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wanding.xingpos.bean.PosScanpayResData;

public class GsonUtils {

	public static Gson getGson(){
		return new Gson();  
	}
	/**
	 * 处理JSON中带Date日期格式的初始化
	 * */
	public static Gson getFormtDateGson(){
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();  
	}
	
	/** 使用方式  */
	private void testMetHod(String jsonStr){
		//集合解析
		List<String> lsss = new ArrayList<String>();
		lsss.clear();
		Gson gjson1  =  GsonUtils.getGson();
		lsss=gjson1.fromJson(jsonStr, new TypeToken<List<String>>() {  }.getType());
		
		//对象解析
		Gson gjson2  =  GsonUtils.getGson();
		PosScanpayResData user = gjson2.fromJson(jsonStr, PosScanpayResData.class);
	}
}

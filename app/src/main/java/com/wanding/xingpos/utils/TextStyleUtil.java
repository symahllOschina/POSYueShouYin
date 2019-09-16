package com.wanding.xingpos.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * textview改变部分文字的颜色
 */
public class TextStyleUtil {
	/**
	 * textStr： 要改变的text字符串
	 * startPosition：改变字符串的起始位置
	 * endPosition：改变字符串的结束位置
	 * */
	public static SpannableStringBuilder changeStyle(String textStr, int startPosition, int endPosition){
		SpannableStringBuilder style1=new SpannableStringBuilder(textStr);
		 //设置指定位置textview的背景颜色  
//		style.setSpan(new BackgroundColorSpan(Color.RED),2,5,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);    
		//设置指定位置文字的颜色  
		style1.setSpan(new ForegroundColorSpan(Color.BLUE),startPosition,endPosition,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style1;
	}
}

package com.wanding.xingpos.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * 数字操作工具类
 */
public class DecimalUtil {
	
	public static String doubletoString(double d) {
		/**
		 *  BigDecimal.setScale()方法用于格式化小数点
		 *	setScale(1)表示保留一位小数，默认用四舍五入方式
		 *	setScale(1,BigDecimal.ROUND_DOWN)直接删除多余的小数位，如2.35会变成2.3
		 *	setScale(1,BigDecimal.ROUND_UP)进位处理，2.35变成2.4
		 *	setScale(1,BigDecimal.ROUND_HALF_UP)四舍五入，2.35变成2.4
		 *	setScaler(1,BigDecimal.ROUND_HALF_DOWN)四舍五入，2.35变成2.3，如果是5则向下舍
		 * */
		BigDecimal b = new BigDecimal(d);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return String.valueOf(f1);
		
	}
	
	/**  
	 *  金额换算（元转分）
	 *  例如：1  转换结果：0.01
	 */
	public static String elementToBranch(String moneyStr){
//		Log.e("原字符串金额：", moneyStr);
		String total_fee = "";
    	BigDecimal sumAmount = new BigDecimal(moneyStr);
    	BigDecimal transAmt = sumAmount.multiply(new BigDecimal(100));//乘以100(单位：分) //String  转化为 BigDecimal ,乘以100，元转化为分进制是100
    	total_fee = transAmt.intValue()+""; 
//    	Log.e("换算的提交金额：", total_fee);
		return total_fee;
	}
	
	/**  
	 *  金额换算（分转元）
	 */
	public static String branchToElement(String moneyStr) {
		Log.e("原字符串金额：",moneyStr);
		String total_fee = "";
		BigDecimal sumAmount = new BigDecimal(moneyStr);
		BigDecimal transAmt = sumAmount.multiply(new BigDecimal(0.01)).setScale(2,BigDecimal.ROUND_HALF_UP);
		total_fee = transAmt.toString();
		Log.e("换算后金额：",total_fee);
		return total_fee;
	}



    /**
	 * 自动补齐金额小数点后两位
	 * 解决GSON转String金额时，默认转成整数问题，例如将0.00或1.00转成0或1，
	 * 同时也可以将String字符串转换为String类型的金额
	 * 例如将字符串.3或2.03，2.156的字符串
	 * 输出为0.3,2.03,2.156
	 */
	public static String StringToPrice(String totalStr){
		BigDecimal returnNum = new BigDecimal(totalStr).setScale(2,BigDecimal.ROUND_HALF_UP);
		return returnNum.toString();
	}

	/**
     * 作用同上：都是将String字符串转换为金额，
     * 不同的是该方法保留小数点后面两位小数，保留采取四舍五入，位数不够使用0补替
     * 例如将字符串.3或2.03，2.156的字符串
	 * 输出为0.30,2.03,2.16
     */
    public static String StringToDoublePrice(String priceStr){
    	double cny = Double.parseDouble(priceStr);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(cny);
    }
    
    /**
     * 判断字符串是否相等（用于金额比较，这里主要作用是判断输入金额是否为有效金额，比如0.00为无效金额）
     * 在数字上小于、等于或大于 b 时，返回 -1、0 或 1。
     * 例如：str = "0.01"; 则返回1
     */
    public static int isEqual(String total_fee){
    	double b = 0.00;
    	BigDecimal data1 = new BigDecimal(total_fee);
    	BigDecimal data2 = new BigDecimal(b);
    	return data1.compareTo(data2);
    }

    /**
	 * isRange（）判断是否在范围内，是返回true
	 */
    public static boolean isRange(double num,int minNum,int maxNum){
    	if(num >= minNum && num<=maxNum){
    		return true;
		}
		return false;
	}
	
	
	/**
	 *  用正则表达式，判断是否为整数
	 */
    public static boolean isNumeric(String str){
        Pattern pattern = compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){  
            return false;   
        }   
        return true;   
     } 
    
    
}

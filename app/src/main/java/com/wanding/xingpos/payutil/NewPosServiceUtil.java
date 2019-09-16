package com.wanding.xingpos.payutil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.wanding.xingpos.bean.UserLoginResData;

/**
 * 调用新大陆POS内置服务接口，支付，查询业务类
 */
public class NewPosServiceUtil {

	/**
	 * 签到请求码
	 */
	public static final int SIGN_REQUEST_CODE = 110;
	/**
	 * 支付请求码
	 */
	public static final int PAY_REQUEST_CODE = 1;


	public static void signInReq(Activity activity) throws Exception{

		ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		Intent intent = new Intent();
		intent.setComponent(component);
		Bundle bundle = new Bundle();
		bundle.putString("msg_tp",  "0300");
		bundle.putString("pay_tp",  "2");
		bundle.putString("order_no",  "");
		bundle.putString("appid",     "com.wanding.xingpos");
		bundle.putString("reason",     "");
		bundle.putString("txndetail",     "");
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, SIGN_REQUEST_CODE);

	}
	
	/**
	 * 支付
	 * payType:支付类型，默认不限，0银行卡，1扫码，11微信，12支付宝，13银联
	 * 
	 */
	public static void payReq(Activity activity, String payType, String total_fee, String order_noStr, UserLoginResData posPublicData){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
		  //String payType = "";//银行卡，微信，支付宝，银联二维码分别顺序对应：040,010,020,030
		    if("040".equals(payType)){
		    	bundle.putString("pay_tp",  "0");//银行卡
		    	bundle.putString("proc_cd",  "000000");
		    }else if("010".equals(payType)){
		    	bundle.putString("pay_tp",  "11");//微信
		    	bundle.putString("proc_cd",  "660000");
		    }else if("020".equals(payType)){
		    	bundle.putString("proc_cd",  "660000");
		    	bundle.putString("pay_tp",  "12");//支付宝
		    }else if("060".equals(payType)){
		    	bundle.putString("pay_tp",  "13");//银联
		    	bundle.putString("proc_cd",  "660000");
		    } 
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  total_fee);
		    //设备号
		    bundle.putString("order_no",  order_noStr);
		    Log.e("生成的订单号：", order_noStr);
		    bundle.putString("batchbillno", "");//流水号：batchbillno=批次号+凭证号（只有退款请求时输入）
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
		    //TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
		    //TODO:
			Log.e("Exception：", "异常");
		}
	}
	
	/**
	 * 扫码退款
	 * total_fee: 退款金额
	 */
	public static void refundReq(Activity activity, String etOrderIdTextStr, String total_fee, UserLoginResData posPublicData){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
		    bundle.putString("pay_tp",  "");
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("proc_cd",  "680000");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  total_fee);
		    bundle.putString("order_no",  etOrderIdTextStr);
		    bundle.putString("batchbillno", "");
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
		    //TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
		    //TODO:
			Log.e("Exception：", "异常");
		}
	}
	/**
	 * 刷卡退款
	 * etOrderIdTextStr: 凭证号，不是订单号
	 */
	public static void cardRefundReq(Activity activity, String etOrderIdTextStr){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp",  "0200");
			bundle.putString("pay_tp",  "");
			bundle.putString("proc_tp",  "00");
			
			bundle.putString("proc_cd",  "200000");
			bundle.putString("systraceno",  etOrderIdTextStr);
			bundle.putString("amt",  "");
			//订单号：退款不传
			bundle.putString("order_no",  "");
			//流水号：凭证号（只有退款请求时输入）
			bundle.putString("batchbillno", "");
			bundle.putString("appid",     "com.wanding.xingpos");
			bundle.putString("reason",     "");
			bundle.putString("txndetail",     "");
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			//TODO:
			Log.e("Exception：", "异常");
		}
	}
	/**
	 * 扫码查询
	 */
	public static void scanQueryReq(Activity activity, String etOrderIdTextStr, UserLoginResData posPublicData){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp",  "0300");
			bundle.putString("pay_tp",  "1");
			//订单号：
			bundle.putString("order_no",  etOrderIdTextStr);
			bundle.putString("appid",     "com.wanding.xingpos");
			bundle.putString("reason",     "");
			bundle.putString("txndetail",     "");
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			//TODO:
			Log.e("Exception：", "异常");
		}
	}
	/**
	 * 扫码查询
	 */
	public static void cardQueryReq(Activity activity, String etOrderIdTextStr, UserLoginResData posPublicData){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp",  "0300");
			bundle.putString("pay_tp",  "0");
			//订单号：
			bundle.putString("order_no",  etOrderIdTextStr);
			bundle.putString("appid",     "com.wanding.xingpos");
			bundle.putString("reason",     "");
			bundle.putString("txndetail",     "");
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			//TODO:
			Log.e("Exception：", "异常");
		}
	}
	
	
	/**
	 * 预授权300000,预授权完成-330000, 预授权撤销-400000 ,预授权完成撤销-440000
	 * 
	 */
	public static void authReq(Activity activity, String type, String total_fee, String order_noStr){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
	    	bundle.putString("pay_tp",  "1");
		    if("1".equals(type)){
		    	bundle.putString("proc_cd",  "300000");
		    }else if("2".equals(type)){
				bundle.putString("proc_cd",  "400000");
		    }else if("3".equals(type)){
				bundle.putString("proc_cd",  "330000");
		    }else if("4".equals(type)){
		    	bundle.putString("proc_cd",  "440000");
		    } 
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  total_fee);
		    //设备号
		    bundle.putString("order_no",  order_noStr);
		    Log.e("生成的订单号：", order_noStr);
		    bundle.putString("batchbillno", "");//流水号：batchbillno=批次号+凭证号（只有退款请求时输入）
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
		    //TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
		    //TODO:
			Log.e("Exception:","异常");

		}
	}

	/**
	 * 结算
	 *
	 */
	public static void settleReq(Activity activity){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp", "0200");
			bundle.putString("proc_tp", "00");
			bundle.putString("proc_cd", "900000");
			bundle.putString("appid", "com.wanding.xingpos");//
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			//TODO:
			Log.e("Exception:","异常");

		}
	}
}

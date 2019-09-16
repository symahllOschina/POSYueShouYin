package com.wanding.xingpos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.wanding.xingpos.utils.SharedPreferencesUtil;

import org.xutils.view.annotation.ContentView;

/**
 * 欢迎界面（主要初始化一些应用数据到保存到本地）
 */
@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	
	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
	
	private void initData(){
		/**
		 * wxPayServiceType默认为true，代表微信支付通道选中走默认服务，即自己后台服务，false走星POS机SDK
		 */
		boolean wxPayServiceType = true;
		boolean aliPayServiceType = true;
		boolean ylPayServiceType = false;
		SharedPreferencesUtil sharedPreferencesUtil_t = new SharedPreferencesUtil(activity, "transaction");
		if(sharedPreferencesUtil_t.contain("wxPayServiceKey")){
			//存在
			Log.e("微信支付设置：", "Key存在");
		}else{
			//保存支付通道设置的通道默认值
			sharedPreferencesUtil_t.put("wxPayServiceKey", wxPayServiceType);
			Log.e("微信支付设置：", "Key不存在保存默认");
		}
		if(sharedPreferencesUtil_t.contain("aliPayServiceKey")){
			//存在
			Log.e("支付宝支付设置：", "Key存在");
		}else{
			//保存支付通道设置的通道默认值
			sharedPreferencesUtil_t.put("aliPayServiceKey", aliPayServiceType);
			Log.e("支付宝支付设置：", "Key不存在保存默认");
		}
		if(sharedPreferencesUtil_t.contain("ylPayServiceKey")){
			//存在
			Log.e("银联二维码支付设置：", "Key存在");
		}else{
			//保存支付通道设置的通道默认值
			sharedPreferencesUtil_t.put("ylPayServiceKey", ylPayServiceType);
			Log.e("银联二维码支付设置：", "Key不存在保存默认");
		}
		
		/**
		 * printNum默认为printNumNo，代表不打印， 设置界面值printNumOne表示一联，printNumTwo则为两联
		 * isDefault默认为true,字体大小为默认，false表示字体大小为大
		 */
		String printNum = "printNumOne";
		boolean isDefault = true;
		SharedPreferencesUtil sharedPreferencesUtil_p = new SharedPreferencesUtil(activity, "printing");
		if(sharedPreferencesUtil_p.contain("printNumKey")){
			//存在
			Log.e("打印设置：", "Key存在");
		}else{
			//保存打印设置的默认值
			sharedPreferencesUtil_p.put("printNumKey", printNum);
			Log.e("打印设置：", "Key不存在保存默认");
		}
		if(sharedPreferencesUtil_p.contain("isDefaultKey")){
			//存在
			Log.e("打印字体大小设置：", "Key存在");
		}else{
			//保存打印设置的默认值
			sharedPreferencesUtil_p.put("isDefaultKey", isDefault);
			Log.e("打印字体大小设置：", "Key不存在保存默认");
		}
		
		/**
		 * 扫码摄像头设置参数值   默认true，代表后置摄像头,前置为false
		 */
		boolean cameType = true;
		SharedPreferencesUtil sharedPreferencesUtil_s = new SharedPreferencesUtil(activity, "scancamera");
		if(sharedPreferencesUtil_s.contain("cameTypeKey")){
			//存在
			Log.e("摄像头设置：", "Key存在");
		}else{
			//不存在保存默认值
			sharedPreferencesUtil_s.put("cameTypeKey", cameType);
			Log.e("摄像头设置：", "Key不存在保存默认");
		}



		/**
		 * pos初始化数据
		 * 应用初始化（获取POS机商户信息）
		 * 执行签到获取参数
		 */
		boolean isPrint = false;
		Intent intent = new Intent();
		intent.setClass(activity,SignInActivity.class);
		intent.putExtra("isPrint",isPrint);//签到成功时是否打印
		startActivity(intent);
		finish();

	}

	

	






	

}

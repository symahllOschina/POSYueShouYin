package com.wanding.xingpos.auth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wanding.xingpos.MainBaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.RandomStringGenerator;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

/** 
 * 预授权Activity(新大陆POS机预授权操作界面)
 */
@ContentView(R.layout.activity_auth)
public class NewLandAuthActivity extends MainBaseActivity implements OnClickListener {

	@ViewInject(R.id.menu_title_imageView)
	ImageView imgBack;
	@ViewInject(R.id.menu_title_layout)
	LinearLayout titleLayout;
	@ViewInject(R.id.menu_title_tvTitle)
	TextView tvTitle;
	@ViewInject(R.id.menu_title_imgTitleImg)
	ImageView imgTitleImg;
	@ViewInject(R.id.menu_title_tvOption)
	TextView tvOption;

	@ViewInject(R.id.newland_auth_tvOk)
	private TextView tvOk;




	/**
	 * authType:1：预授权300000，2：预授权完成-330000，3：预授权撤销-400000，4：预授权完成撤销-440000
	 */
	private String authType;
	/**
	 * 签到登录商户信息
	 */
	private UserLoginResData loginInitData;


	/**
	 * 各支付通道不同的流水号
	 */
	private String pos_order_noStr;
	

    





	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("预授权");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		Intent intent = getIntent();
		authType = intent.getStringExtra("authType");
		Log.e("操作区分码：",authType);
		loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");


		initListener();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//清空StringBuilder，EditText恢复初始值
		 //清空EditText
		//清空StringBuilder，EditText恢复初始值
		//清空EditText


		pending.delete( 0, pending.length() );
		if(pending.length()<=0){
			etSumMoney.setText("￥0.00");
		}
		//取出保存的默认支付金额
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(activity, "defMoneyNum");
		//取出保存的默认值
		String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
		if("".equals(defMoney) || "0".equals(defMoney)){
			etSumMoney.setHint("￥0.00");
		}else{
			etSumMoney.setText("￥"+defMoney);
			pending.append(defMoney);
		}



	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}

	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandScanPayResult(Bundle bundle){
		try {
			String transamount = "";
			String msgTp = bundle.getString("msg_tp");
			if (TextUtils.equals(msgTp, "0210")) {
				String txndetail = bundle.getString("txndetail");
				Log.e("txndetail支付返回信息：", txndetail);

				JSONObject job = new JSONObject(txndetail);
				transamount = job.getString("transamount");


				Gson gjson  =  GsonUtils.getGson();
				CardPaymentDate posResult = gjson.fromJson(txndetail, CardPaymentDate.class);

				savaOrderPrintText(posResult);
				Log.e("返回的预授权金额信息：", transamount);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void savaOrderPrintText(CardPaymentDate posResult){
		/**
		 * 下面是调用帮助类将一个对象以序列化的方式保存
		 * 方便我们在其他界面调用，类似于Intent携带数据
		 */
		try {
			MySerialize.saveObject("cardPayOrder",activity,MySerialize.serialize(posResult));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
		Boolean cardPayValue = true;
		int cardOption = 1;
		sharedPreferencesUtil.put("cardPayYes", cardPayValue);
		sharedPreferencesUtil.put("cardPayType", "pay");
		sharedPreferencesUtil.put("cardOption", cardOption);

		//清空StringBuilder，EditText恢复初始值
		//清空EditText
		pending.delete( 0, pending.length() );
		if(pending.length()<=0){
			etSumMoney.setText("￥0.00");
		}


	}

	/**
	 * 发起预授权第一步
	 */
	private void payMethodOne(){
		try {
			String etTotal_fee = pending.toString();
			Log.e("输入框金额值：", etTotal_fee);
			if(Utils.isEmpty(etTotal_fee)){
				ToastUtil.showText(activity,"请输入有效金额！",1);
				return;
			}
			String total_feeStr =  DecimalUtil.StringToPrice(etTotal_fee);
			Log.e("金额值转换后：", etTotal_fee);
			//金额是否合法
			int isCorrect = DecimalUtil.isEqual(total_feeStr);
			if(isCorrect != 1){
				ToastUtil.showText(activity,"请输入有效金额！",1);
				return;
			}
			payMethodTwo(total_feeStr);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	/**
	 * 发起预授权第二步
	 */
	private void payMethodTwo(String total_fee){
		String deviceNum = loginInitData.getTrmNo_pos();
		pos_order_noStr = RandomStringGenerator.getNlRandomNum(deviceNum);
		Log.e("生成的订单号：", pos_order_noStr);
		NewPosServiceUtil.authReq(NewLandAuthActivity.this, authType, total_fee,pos_order_noStr);
	}



	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()){
			case R.id.menu_title_imageView:
				finish();
				break;
			case R.id.newland_auth_tvOk:
				payMethodOne();
				break;
				default:
					break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			Bundle bundle=data.getExtras();
			if (requestCode == NewPosServiceUtil.PAY_REQUEST_CODE&&bundle != null) {
				switch (resultCode) {
					// 支付成功
					case Activity.RESULT_OK:
						newlandScanPayResult(bundle);
						break;
					// 支付取消
					case Activity.RESULT_CANCELED:
						String reason = "操作已取消";
						reason = bundle.getString("reason");
						Log.e("失败返回值", reason);
						if (reason != null) {
							Log.d("reason", reason);
							ToastUtil.showText(activity,reason,1);
						}
						//清空StringBuilder，EditText恢复初始值
						//清空EditText
						pending.delete( 0, pending.length() );
						if(pending.length()<=0){
							etSumMoney.setText("￥0.00");
						}
						break;

						default:
							break;

				}
			}
			

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

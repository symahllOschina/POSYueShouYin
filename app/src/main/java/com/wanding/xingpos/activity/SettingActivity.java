package com.wanding.xingpos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/** 设置界面 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements OnClickListener {

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



	/**
	 * 交易设置,打印设置,金额设置
	 * 商户信息，清空流水，关于悦收银
	 */
	@ViewInject(R.id.setting_transaction_layout)
	RelativeLayout transactionlayout;
	@ViewInject(R.id.setting_print_layout)
	RelativeLayout printLayout;
	@ViewInject(R.id.setting_money_layout)
	RelativeLayout defMoneyLayout;
	@ViewInject(R.id.setting_business_layout)
	RelativeLayout businessLayout;
	@ViewInject(R.id.setting_clearWater_layout)
	RelativeLayout clearWaterLayout;
	@ViewInject(R.id.setting_version_layout)
	RelativeLayout versionlayout;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private String posProvider;
	private UserLoginResData loginInitData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("设置");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");
		posProvider = MainActivity.posProvider;

		Intent intent = getIntent();
		loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

		initListener();
	}
	
	/** 
	 * 注册按钮事件
	 */
	private void initListener(){

		imgBack.setOnClickListener(this);
		transactionlayout.setOnClickListener(this);
		printLayout.setOnClickListener(this);
		defMoneyLayout.setOnClickListener(this);
		businessLayout.setOnClickListener(this);
		clearWaterLayout.setOnClickListener(this);
		versionlayout.setOnClickListener(this);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();
		if (requestCode == 1&&bundle != null) {
			switch (resultCode) {
				// 支付成功
				case Activity.RESULT_OK:

					break;
				// 支付取消
				case Activity.RESULT_CANCELED:
					String reason = bundle.getString("reason");
					Log.e("失败返回值", reason);
					if (Utils.isNotEmpty(reason)) {
                        if("无交易记录,无需结算".equals(reason)){
							ToastUtil.showText(activity,"无交易记录！",1);
                        }
					}

					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.setting_transaction_layout://交易设置
			in = new Intent();
			in.setClass(this, SetPayServiceActivity.class);
			startActivity(in);
			break;
		case R.id.setting_print_layout://打印设置
			in = new Intent();
			in.setClass(this, SetPrintNumActivity.class);
			startActivity(in);
			break;
		case R.id.setting_money_layout://默认支付金额设置
			in = new Intent();
			in.setClass(this, SetMoneyActivity.class);
			startActivity(in);
			break;
		case R.id.setting_business_layout://商户信息
			in = new Intent();
			in.setClass(this, BusinessDetailsActivity.class);
			in.putExtra("userLoginData",loginInitData);
			startActivity(in);
			break;
		case R.id.setting_clearWater_layout://清空流水
			if(posProvider.equals(Constants.NEW_LAND)){
				NewPosServiceUtil.settleReq(activity);
			}else if(posProvider.equals(Constants.FUYOU_SF)){
				FuyouPosServiceUtil.settleReq(activity);
			}
			break;
		case R.id.setting_version_layout://关于我们
			in = new Intent();
			in.setClass(this, AboutUsActivity.class);
			startActivity(in);
			break;
			default:
				break;
		
		}
	}
}

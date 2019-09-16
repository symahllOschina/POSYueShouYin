package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.utils.SharedPreferencesUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/** 交易设置界面(选择支付通道) */
@ContentView(R.layout.activity_setting_payservice)
public class SetPayServiceActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener {


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

	@ViewInject(R.id.setting_payservice_ylewmLayout)
	LinearLayout ylewmLayout;
	@ViewInject(R.id.setting_payservice_wxRadioGroup)
	RadioGroup wxRadioGroup;
	@ViewInject(R.id.setting_payservice_aliRadioGroup)
	RadioGroup aliRadioGroup;
	@ViewInject(R.id.setting_payservice_ylewmRadioGroup)
	RadioGroup ylRadioGroup;
	@ViewInject(R.id.setting_payservice_cameraRadioGroup)
	RadioGroup cameRadioGroup;
	@ViewInject(R.id.setting_payservice_wxBtnDefault)
	RadioButton wxBtnDefault;
	@ViewInject(R.id.setting_payservice_wxBtnOther)
	RadioButton wxBtnOther;
	@ViewInject(R.id.setting_payservice_aliBtnDefault)
	RadioButton aliBtnDefault;
	@ViewInject(R.id.setting_payservice_aliBtnOther)
	RadioButton aliBtnOther;
	@ViewInject(R.id.setting_payservice_ylewmBtnDefault)
	RadioButton ylBtnDefault;
	@ViewInject(R.id.setting_payservice_ylewmBtnOther)
	RadioButton ylBtnOther;
	@ViewInject(R.id.setting_payservice_frontCamera)
	RadioButton frontCameBtn;
	@ViewInject(R.id.setting_payservice_postCamera)
	RadioButton postCameBtn;


	@ViewInject(R.id.setting_payservice_tvOK)
	TextView tvOk;
	
	private boolean wxPayServiceType = true;
	private boolean aliPayServiceType = true;
	private boolean ylPayServiceType = true;

	/** 扫码摄像头设置参数值   默认true，代表后置摄像头,前置为false  */
	private boolean cameType = true;
	
	private SharedPreferencesUtil sharedPreferencesUtil1;
	private SharedPreferencesUtil sharedPreferencesUtil2;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private String posProvider;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("交易设置");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		posProvider = MainActivity.posProvider;
		initView();
		initListener();
		initData();
	}

	/**
	 * 初始化界面控件
	 */
	private void initView(){

		ylewmLayout.setVisibility(View.GONE);
		if(posProvider.equals(Constants.FUYOU_SF)){
			ylewmLayout.setVisibility(View.VISIBLE);
		}
	}

	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		wxRadioGroup.setOnCheckedChangeListener(this);
		aliRadioGroup.setOnCheckedChangeListener(this);
		ylRadioGroup.setOnCheckedChangeListener(this);
		cameRadioGroup.setOnCheckedChangeListener(cameRGCheckedListener);
	}
	
	/**  初始化数据 */
	private void initData(){
		//transaction ：交易设置值存储应用本地的文件名称
		sharedPreferencesUtil1 = new SharedPreferencesUtil(activity, "transaction");
		//取出保存的值
		wxPayServiceType = (Boolean) sharedPreferencesUtil1.getSharedPreference("wxPayServiceKey", true);
		aliPayServiceType = (Boolean) sharedPreferencesUtil1.getSharedPreference("aliPayServiceKey", true);
		ylPayServiceType = (Boolean) sharedPreferencesUtil1.getSharedPreference("ylPayServiceKey", true);
		if(wxPayServiceType)
		{
			wxBtnDefault.setChecked(true);
			wxBtnOther.setChecked(false);
		}else{
			wxBtnDefault.setChecked(false);
			wxBtnOther.setChecked(true);
		}
		if(aliPayServiceType)
		{
			aliBtnDefault.setChecked(true);
			aliBtnOther.setChecked(false);
		}else{
			aliBtnDefault.setChecked(false);
			aliBtnOther.setChecked(true);
		}
		if(ylPayServiceType)
		{
			ylBtnDefault.setChecked(true);
			ylBtnOther.setChecked(false);
		}else{
			ylBtnDefault.setChecked(false);
			ylBtnOther.setChecked(true);
		}
		
		//取出保存的摄像头参数值
		sharedPreferencesUtil2 = new SharedPreferencesUtil(activity, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil2.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			frontCameBtn.setChecked(false);
			postCameBtn.setChecked(true);
		}else{
			frontCameBtn.setChecked(true);
			postCameBtn.setChecked(false);
		}
	}
	
	


	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.setting_payservice_tvOK:
			
			if(wxPayServiceType){
				Log.e("微信选则的支付通道为：", "默认");
			}else{
				Log.e("微信选则的支付通道为：", "第三方");
			}
			if(aliPayServiceType){
				Log.e("支付宝选则的支付通道为：", "默认");
			}else{
				Log.e("支付宝选则的支付通道为：", "第三方");
			}
			if(ylPayServiceType){
				Log.e("银联二维码选则的支付通道为：", "默认");
			}else{
				Log.e("银联二维码选则的支付通道为：", "第三方");
			}
			if(cameType){
				Log.e("摄像头选择的为：", "后置");
			}else{
				Log.e("摄像头选择的为：", "前置");
			}
			//保存支付通道设置的通道值
			sharedPreferencesUtil1.put("wxPayServiceKey", wxPayServiceType);
			sharedPreferencesUtil1.put("aliPayServiceKey", aliPayServiceType);
			sharedPreferencesUtil1.put("ylPayServiceKey", ylPayServiceType);
			//保存调用摄像头的值
			sharedPreferencesUtil2.put("cameTypeKey", cameType);
			finish();
			break;
			default:
				break;
		
		}
	}

	/** RadioGroup选中事件监听 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (group.getId()) {
		case R.id.setting_payservice_wxRadioGroup://微信选择监听
			if(checkedId == R.id.setting_payservice_wxBtnDefault){
				wxPayServiceType = true;
			}else{
				wxPayServiceType = false;
			}
			break;
		case R.id.setting_payservice_aliRadioGroup://支付宝选择监听
			if(checkedId == R.id.setting_payservice_aliBtnDefault){
				aliPayServiceType = true;
			}else{
				aliPayServiceType = false;
			}
			break;
		case R.id.setting_payservice_ylewmRadioGroup://银联二维码选择监听
			if(checkedId == R.id.setting_payservice_ylewmBtnDefault){
				ylPayServiceType = true;
			}else{
				ylPayServiceType = false;
			}
			break;
			default:
				break;
		}
	}

	private OnCheckedChangeListener cameRGCheckedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(checkedId == R.id.setting_payservice_postCamera){
				cameType = true;
			}else{
				cameType = false;
			}
		}
	};

}

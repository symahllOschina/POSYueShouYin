package com.wanding.xingpos.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.utils.EditTextUtils;
import com.wanding.xingpos.utils.SharedPreferencesUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 默认支付金额设置界面
 */
@ContentView(R.layout.activity_setting_money)
public class SetMoneyActivity extends BaseActivity implements OnClickListener {
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

	@ViewInject(R.id.setting_money_etMoney)
	private EditText etMoney;
	@ViewInject(R.id.setting_money_tvOK)
	private TextView tvOk;



	
	//默认金额为0
	private String defMoney = "0";
	private SharedPreferencesUtil sharedPreferencesUtil;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("设置默认金额");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		initView();
		initListener();
	}
	

	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		//设置设置金额输入框输入规则（只能输入金额字符）
		EditTextUtils.setPricePoint(etMoney);

		//defMoneyNum ：交易设置值存储应用本地的文件名称
		sharedPreferencesUtil = new SharedPreferencesUtil(activity, "defMoneyNum");
		//取出保存的默认值
		defMoney = (String) sharedPreferencesUtil.getSharedPreference("defMoneyKey", "");
		etMoney.setText(defMoney);
		
	}

	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.setting_money_tvOK:
			String etMoneyStr = etMoney.getText().toString().trim();
			if("".equals(etMoneyStr)){
				//保存支付通道设置的通道值
				sharedPreferencesUtil.put("defMoneyKey", "0");
			}else if("0.00".equals(etMoneyStr)){
				sharedPreferencesUtil.put("defMoneyKey", "0");
			}else{
				//保存支付通道设置的通道值
				sharedPreferencesUtil.put("defMoneyKey", etMoneyStr);
			}
			
			finish();
			break;
			default:
				break;
		
		}
	}

	
}

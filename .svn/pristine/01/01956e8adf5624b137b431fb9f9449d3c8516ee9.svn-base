package com.wanding.xingpos.activity;
import android.content.Context;
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
import com.wanding.xingpos.R;
import com.wanding.xingpos.utils.SharedPreferencesUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/** 打印设置界面(选择打印联数) */
@ContentView(R.layout.activity_setting_printnum)
public class SetPrintNumActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener {

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

	@ViewInject(R.id.setting_printnum_numRadioGroup)
	RadioGroup numRadioGroup;
	@ViewInject(R.id.setting_printnum_textSizeRadioGroup)
	RadioGroup textSizeRadioGroup;
	@ViewInject(R.id.setting_printnum_numButNo)
	RadioButton numButNo;
	@ViewInject(R.id.setting_printnum_numButOne)
	RadioButton numButOne;
	@ViewInject(R.id.setting_printnum_numButTwo)
	RadioButton numButTwo;
	@ViewInject(R.id.setting_printnum_textButDefault)
	RadioButton textButDefault;
	@ViewInject(R.id.setting_printnum_textButLarge)
	RadioButton textButLarge;

	@ViewInject(R.id.setting_printnum_tvOK)
	TextView tvOk;



	//默认是一联printNumOne
	private String printNum = "printNumNo";
	//字体大小默认
	private boolean isDefault = true;
	private SharedPreferencesUtil sharedPreferencesUtil;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("打印设置");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		initListener();
		initData();
	}

	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		numRadioGroup.setOnCheckedChangeListener(this);
		textSizeRadioGroup.setOnCheckedChangeListener(this);
	}

	
	/**  初始化数据 */
	private void initData(){
		//printing ：交易设置值存储应用本地的文件名称
		sharedPreferencesUtil = new SharedPreferencesUtil(activity, "printing");
		//取出保存的值
		printNum = (String) sharedPreferencesUtil.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil.getSharedPreference("isDefaultKey", isDefault);
		if("printNumNo".equals(printNum)){
			numButNo.setChecked(true);
			numButOne.setChecked(false);
			numButTwo.setChecked(false);
		}else if("printNumOne".equals(printNum))
		{
			numButNo.setChecked(false);
			numButOne.setChecked(true);
			numButTwo.setChecked(false);
		}else{
			numButNo.setChecked(false);
			numButOne.setChecked(false);
			numButTwo.setChecked(true);
		}

		if(isDefault){
			textButDefault.setChecked(true);
			textButLarge.setChecked(false);
		}else{
			textButDefault.setChecked(false);
			textButLarge.setChecked(true);
		}

		
	}
	


	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.setting_printnum_tvOK:
			if("printNumNo".equals(printNum)){
				Log.e("打印联数设置为：", "printNumNo");
			}else if("printNumOne".equals(printNum)){
				Log.e("打印联数设置为：", "printNumOne");
			}else{
				Log.e("打印联数设置为：", "printNumTwo");
			}

			if(isDefault){
				Log.e("打印字体大小设置为：", "isDefault");
			}else{
				Log.e("打印字体大小设置为：", "isDefault");
			}
			//保存支付通道设置的通道值
			sharedPreferencesUtil.put("printNumKey", printNum);
			sharedPreferencesUtil.put("isDefaultKey", isDefault);
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
		case R.id.setting_printnum_numRadioGroup://
			if(checkedId == R.id.setting_printnum_numButNo){
				printNum = "printNumNo";
			}else if(checkedId == R.id.setting_printnum_numButOne){
				printNum = "printNumOne";
			}else{
				printNum = "printNumTwo";
			}
			break;
		case R.id.setting_printnum_textSizeRadioGroup://
			if(checkedId == R.id.setting_printnum_textButDefault){
				isDefault = true;
			}else{
				isDefault = false;
			}
			break;
			default:
				break;
		}
	}
}

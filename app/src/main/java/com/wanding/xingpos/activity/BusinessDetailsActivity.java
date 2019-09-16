package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/** 商户信息展示Activity  */
@ContentView(R.layout.activity_business_details)
public class BusinessDetailsActivity extends BaseActivity implements OnClickListener {

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
	

	
	/** 商户信息 */
	@ViewInject(R.id.business_details_tvMerchantName)
	TextView tvMerchantName;
	@ViewInject(R.id.business_details_tvMerchant_no)
	TextView tvMerchantNo;
	@ViewInject(R.id.business_details_tvKuanTaiName)
	TextView tvKuanTaiName;
	@ViewInject(R.id.business_details_tvTerminal_id)
	TextView tvTerminalId;
	@ViewInject(R.id.business_details_tvMercId_pos)
	TextView tvMercId_pos;
	@ViewInject(R.id.business_details_tvTrmNo_pos)
	TextView tvTrmNo_pos;
	@ViewInject(R.id.business_details_tvBatchno_pos)
	TextView tvBatchno_pos;
	
	/** 完成 */
	@ViewInject(R.id.business_details_tvBack)
	TextView tvBack;

	
	private UserLoginResData posPublicData;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("商户信息");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");


		Intent intent = getIntent();
		posPublicData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

		initListener();

		updateViewData();
		
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	
    

	
	/** 
	 * 初始化界面控件
	 */
	private void initListener(){

		imgBack.setOnClickListener(this);
		tvBack.setOnClickListener(this);
	}
	

	/** 界面数据初始化 */
    private void updateViewData(){
    	
    	try {
    		tvMerchantName.setText("");
			tvMerchantNo.setText("");
			tvKuanTaiName.setText("");
			tvTerminalId.setText("");
			tvMercId_pos.setText("");
			tvTrmNo_pos.setText("");
			tvBatchno_pos.setText("");
			
			if(posPublicData!=null){
				//商户名称
				tvMerchantName.setText(posPublicData.getMername_pos());
				//商户号
				tvMerchantNo.setText(posPublicData.getMerchant_no());
				//款台名称
				tvKuanTaiName.setText(posPublicData.getEname());
				//设备号
				tvTerminalId.setText(posPublicData.getTerminal_id());
				//pos商户号
				tvMercId_pos.setText(posPublicData.getMercId_pos());
				//pos设备号
				tvTrmNo_pos.setText(posPublicData.getTrmNo_pos());
				//批次号
				tvBatchno_pos.setText(posPublicData.getBatchno_pos());
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.business_details_tvBack:
			finish();
			break;
			default:
				break;
		}
	}
}

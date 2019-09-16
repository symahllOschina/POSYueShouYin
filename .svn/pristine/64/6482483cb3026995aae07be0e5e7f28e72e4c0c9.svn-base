package com.wanding.xingpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;

/**  退款成功提示界面 */
public class RefundSuccessActivity extends BaseActivity implements OnClickListener{

	private Context context = RefundSuccessActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	private TextView tvOk;

	
	private static String TAG = "lyc";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refund_success_activity);
		initData();
		initView();
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	
	/** 初始化数据 */
	private void initData(){

	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvOk = (TextView) findViewById(R.id.refund_success_tvOK);
		
		tvTitle.setText("退款");
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}




	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.refund_success_tvOK:
			finish();
			break;
			
		}
	}
}

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
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**  支付成功提示界面 */
@ContentView(R.layout.activity_pay_success)
public class PaySuccessActivity extends BaseActivity implements OnClickListener {


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
	

    @ViewInject(R.id.pay_success_payOrderIdText)
	private TextView tvPayOrderId;
    @ViewInject(R.id.pay_success_tvOK)
	private TextView tvOk;
	

    /**
     *  订单号
     */
	private String pos_order_noStr;
	
	

	
    
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("收银");
        Intent in = getIntent();
        if("test".equals(NitConfig.isTest)){
            pos_order_noStr = in.getStringExtra("pos_order");
            tvPayOrderId.setVisibility(View.VISIBLE);
            tvPayOrderId.setText(pos_order_noStr);
        }

        initListener();


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
		case R.id.pay_success_tvOK:
			finish();
			break;
			default:
			    break;
			
		}
	}
}

package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 退款管理界面（银行卡退款，扫码退款）
 */
@ContentView(R.layout.activity_refund_manage)
public class  RefundManageActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.refund_manage_cardRefundLayout)
    RelativeLayout cardRefundLayout;
    @ViewInject(R.id.refund_manage_scanRefundLayout)
    RelativeLayout scanRefundLayout;

    private UserLoginResData posPublicData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("退款");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        Intent intent = getIntent();
        posPublicData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initListener();
    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        cardRefundLayout.setOnClickListener(this);
        scanRefundLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.refund_manage_cardRefundLayout:
                intent.setClass(activity, CardRefundActivity.class);
                intent.putExtra("userLoginData",posPublicData);
                startActivity(intent);
                break;
            case R.id.refund_manage_scanRefundLayout:
                intent.setClass(activity, ScanRefundActivity.class);
                intent.putExtra("userLoginData",posPublicData);
                startActivity(intent);


//                ToastUtil.showText(activity,"退款成功！",true,2);

                break;
                default:
                    break;
        }
    }
}

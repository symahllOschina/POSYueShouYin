package com.wanding.xingpos.card.stock.activity;

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

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 卡劵管理Activity
 */
@ContentView(R.layout.activity_card_stock_manage)
public class CardStockManageActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.card_stock_manage_batchCardStockLayout)
    RelativeLayout batchCardStockLayout;

    /**
     * 签到登录商户信息
     */
    private UserLoginResData loginInitData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("卡劵");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);

        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
        initListener();
    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        batchCardStockLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.card_stock_manage_batchCardStockLayout:
                intent = new Intent();
                intent.setClass(activity, BatchCardStockActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
                default:
                    break;

        }
    }
}

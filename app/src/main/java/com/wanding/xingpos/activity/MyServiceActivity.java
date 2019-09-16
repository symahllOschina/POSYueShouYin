package com.wanding.xingpos.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 我的服务商
 */
@ContentView(R.layout.activity_my_service)
public class MyServiceActivity extends BaseActivity implements View.OnClickListener {

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
     * 服务商名称，业务员名称，联系方式，联系地址
     */
    @ViewInject(R.id.my_service_tvCashName)
    TextView tvCashName;
    @ViewInject(R.id.my_service_tvSalesmanName)
    TextView tvSalesmanName;
    @ViewInject(R.id.my_service_tvSalesmanTel)
    TextView tvSalesmanTel;
    @ViewInject(R.id.my_service_tvSalesmanAddress)
    TextView tvSalesmanAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("我的服务商");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        initListener();

        updateView();

    }

    private void initListener(){
        imgBack.setOnClickListener(this);
    }


    private void updateView(){
        tvCashName.setText("");
        tvSalesmanName.setText("");
        tvSalesmanTel.setText("");
        tvSalesmanAddress.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
                default:
                    break;
        }
    }
}


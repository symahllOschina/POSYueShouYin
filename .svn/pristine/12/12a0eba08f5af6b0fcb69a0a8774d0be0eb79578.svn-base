package com.wanding.xingpos.auth.activity;

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
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 预授权管理界面
 */
@ContentView(R.layout.activity_auth_manage)
public class AuthManageActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.auth_manage_authLayout)
    RelativeLayout authLayout;
    @ViewInject(R.id.auth_manage_authConfirmLayout)
    RelativeLayout authConfirmLayout;
    @ViewInject(R.id.auth_manage_authCancelLayout)
    RelativeLayout authCancelLayout;
    @ViewInject(R.id.auth_manage_authConfirmCancelLayout)
    RelativeLayout authConfirmCancelLayout;
    @ViewInject(R.id.auth_manage_scanAuthLayout)
    RelativeLayout scanAuthLayout;
    @ViewInject(R.id.auth_manage_scanAuthConfirmLayout)
    RelativeLayout scanAuthConfirmLayout;
    @ViewInject(R.id.auth_manage_scanAuthCancelLayout)
    RelativeLayout scanAuthCancelLayout;

    @ViewInject(R.id.antu_manage_view5)
    View divView5;
    @ViewInject(R.id.antu_manage_view6)
    View divView6;
    @ViewInject(R.id.antu_manage_view7)
    View divView7;
    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private String posProvider;

    /**
     * 签到登录商户信息
     */
    private UserLoginResData loginInitData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("预授权");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        posProvider = MainActivity.posProvider;
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initView();

        initListener();
    }

    private void initView(){
        if(posProvider.equals(Constants.NEW_LAND)){
            scanAuthLayout.setVisibility(View.GONE);
            scanAuthConfirmLayout.setVisibility(View.GONE);
            scanAuthCancelLayout.setVisibility(View.GONE);
            divView5.setVisibility(View.GONE);
            divView6.setVisibility(View.GONE);
            divView7.setVisibility(View.GONE);
        }
    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        authLayout.setOnClickListener(this);
        authConfirmLayout.setOnClickListener(this);
        authCancelLayout.setOnClickListener(this);
        authConfirmCancelLayout.setOnClickListener(this);
        scanAuthLayout.setOnClickListener(this);
        scanAuthConfirmLayout.setOnClickListener(this);
        scanAuthCancelLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String authType = "";
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.auth_manage_authLayout:
                authType = "1";
                //如果是新大陆PSO预授权需调用自己的界面输入金额
                if(posProvider.equals(Constants.NEW_LAND)){
                    intent.setClass(activity, NewLandAuthActivity.class);
                    intent.putExtra("authType", authType);
                    intent.putExtra("userLoginData",loginInitData);
                    startActivity(intent);
                }else{
                    intent.setClass(activity,AuthActivity.class);
                    intent.putExtra("authType", authType);
                    intent.putExtra("userLoginData",loginInitData);
                    startActivity(intent);
                }
                break;
            case R.id.auth_manage_authConfirmLayout:
                authType = "3";
                intent.setClass(activity,AuthActivity.class);
                intent.putExtra("authType", authType);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.auth_manage_authCancelLayout:
                authType = "2";
                intent.setClass(activity,AuthActivity.class);
                intent.putExtra("authType", authType);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.auth_manage_authConfirmCancelLayout:
                authType = "4";
                intent.setClass(activity,AuthActivity.class);
                intent.putExtra("authType", authType);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.auth_manage_scanAuthLayout:
                ToastUtil.showText(activity,"暂未开通",1);
                break;
            case R.id.auth_manage_scanAuthConfirmLayout:
                ToastUtil.showText(activity,"暂未开通",1);
                break;
            case R.id.auth_manage_scanAuthCancelLayout:
                ToastUtil.showText(activity,"暂未开通",1);
                break;
                default:
                    break;
        }
    }
}

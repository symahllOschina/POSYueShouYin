package com.wanding.xingpos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.activity.MyQRCodeActivity;
import com.wanding.xingpos.activity.MyServiceActivity;
import com.wanding.xingpos.activity.SettingActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("ValidFragment")
@ContentView(R.layout.fragment_my)
public class MainMyFragment extends BaseFragment implements View.OnClickListener{

    @ViewInject(R.id.my_fragment_circleImageView)
    CircleImageView headImg;
    @ViewInject(R.id.my_fragment_myMerName)
    TextView myMerName;
    @ViewInject(R.id.my_fragment_myCashName)
    TextView myCashName;
    @ViewInject(R.id.my_fragment_myCashAccount)
    TextView myCashAccount;

    @ViewInject(R.id.my_fragment_signInLayout)
    RelativeLayout signInLayout;
    @ViewInject(R.id.my_fragment_myServiceLayout)
    RelativeLayout myServiceLayout;
    @ViewInject(R.id.my_fragment_qrcodeLayout)
    RelativeLayout qrcodeLayout;
    @ViewInject(R.id.my_fragment_settLayout)
    RelativeLayout settLayout;
    @ViewInject(R.id.my_fragment_telLayout)
    RelativeLayout telLayout;
    @ViewInject(R.id.my_fragment_helpLayout)
    RelativeLayout helpLayout;

    /**
     * POS初始化信息
     */
    private UserLoginResData loginInitData;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated().........");

        initListener();

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG,"onResume().........");

        try {
            loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initView();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause().........");
    }

    private void initListener(){
        headImg.setOnClickListener(this);
        signInLayout.setOnClickListener(this);
        myServiceLayout.setOnClickListener(this);
        qrcodeLayout.setOnClickListener(this);
        settLayout.setOnClickListener(this);
        helpLayout.setOnClickListener(this);


    }

    private void initView(){
        myMerName.setText("");
        myCashName.setText("款台名称："+"");
        myCashAccount.setText("款台账号："+"");
        if(loginInitData!=null){
            myMerName.setText(loginInitData.getMername_pos()+"("+loginInitData.getStoreName()+")");
            myCashName.setText("款台名称："+loginInitData.getEname());
            myCashAccount.setText("款台账号："+loginInitData.getEaccount());
        }

    }




    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.my_fragment_circleImageView:

                break;
            case R.id.my_fragment_signInLayout:
                boolean isPrint = true;
                intent.setClass(activity,SignInActivity.class);
                intent.putExtra("isPrint",isPrint);//签到成功时是否打印
                startActivity(intent);
                break;
            case R.id.my_fragment_myServiceLayout:
                ToastUtil.showText(activity,"暂未开通！",1);
//                intent.setClass(activity, MyServiceActivity.class);
//                intent.putExtra("userLoginData",loginInitData);
//                startActivity(intent);
                break;
            case R.id.my_fragment_qrcodeLayout:
                ToastUtil.showText(activity,"暂未开通！",1);
//                intent.setClass(activity, MyQRCodeActivity.class);
//                intent.putExtra("userLoginData",loginInitData);
//                startActivity(intent);
                break;
            case R.id.my_fragment_settLayout:

                intent.setClass(activity, SettingActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.my_fragment_helpLayout:
                ToastUtil.showText(activity,"暂未开通！",1);
                break;

            default:

                break;
        }
    }


}

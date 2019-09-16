package com.wanding.xingpos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.wanding.xingpos.activity.AgainPrintActivity;
import com.wanding.xingpos.activity.CashierActivity;
import com.wanding.xingpos.activity.OrderListActivity;
import com.wanding.xingpos.activity.RefundManageActivity;
import com.wanding.xingpos.activity.ShiftActivity;
import com.wanding.xingpos.auth.activity.AuthManageActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.card.stock.activity.CardStockManageActivity;
import com.wanding.xingpos.card.stock.activity.CheckCardStockActivity;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

@ContentView(R.layout.fragment_home)
public class MainHomeFragment extends BaseFragment implements View.OnClickListener{

    @ViewInject(R.id.home_cashier_layout)
    LinearLayout cashierLayout;
    @ViewInject(R.id.home_checkSecurities_layout)
    LinearLayout checkSecuritiesLayout;
    @ViewInject(R.id.home_menu_order_layout)
    LinearLayout orderLayout;
    @ViewInject(R.id.home_menu_auth_layout)
    LinearLayout authLayout;
    @ViewInject(R.id.home_menu_refund_layout)
    LinearLayout refundLayout;
    @ViewInject(R.id.home_menu_shift_layout)
    LinearLayout shiftLayout;
    @ViewInject(R.id.home_menu_cardStock_layout)
    LinearLayout cardStockLayout;
    @ViewInject(R.id.home_menu_againPrint_layout)
    LinearLayout againPrintLayout;


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

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause().........");
    }

    private void initListener(){
        cashierLayout.setOnClickListener(this);
        checkSecuritiesLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        authLayout.setOnClickListener(this);
        refundLayout.setOnClickListener(this);
        shiftLayout.setOnClickListener(this);
        cardStockLayout.setOnClickListener(this);
        againPrintLayout.setOnClickListener(this);
    }



    private void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
            switch (msg.what){
                case NetworkUtils.MSG_WHAT_ONE:
                    String jsonStr=(String) msg.obj;

                    break;
                case NetworkUtils.MSG_WHAT_TWO:
                    String scanPayJsonStr=(String) msg.obj;

                    break;
                case NetworkUtils.MSG_WHAT_THREE:
                    String saveTestJsonStr=(String) msg.obj;

                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.home_cashier_layout://收款
                intent.setClass(activity,CashierActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.home_checkSecurities_layout://核劵
                intent = new Intent();
                intent.setClass(getActivity(), CheckCardStockActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.home_menu_order_layout://订单
                intent.setClass(activity,OrderListActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);

                break;
            case R.id.home_menu_auth_layout://预授权
                intent.setClass(activity,AuthManageActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.home_menu_refund_layout://退款
                intent.setClass(activity, RefundManageActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.home_menu_shift_layout://交接班
                intent.setClass(activity,ShiftActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);

                break;
            case R.id.home_menu_cardStock_layout://卡劵
                intent.setClass(activity,CardStockManageActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            case R.id.home_menu_againPrint_layout://重打印
                intent.setClass(activity,AgainPrintActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);

                break;
            default:

                break;
        }
    }

}

package com.wanding.xingpos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;
import com.wanding.xingpos.http.util.NetWorkChangReceiver;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.utils.SnackbarAction;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 定义父Activtiy
 */
public class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    protected App myApp;
    @ViewInject(R.id.toolbar)
    protected Toolbar mToolbar;
    @ViewInject(R.id.toolbar_title)
    private TextView mToolbarTitle;
    private ProgressDialog waitingDialog;

    protected BaseActivity activity;
    public Map<String,String> map;

    private boolean isRegistered = false;
    private NetWorkChangReceiver netWorkChangReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_layout);
        myApp = (App) getApplication();
        activity = this;
        App.getInstance().addActivity(this);
        x.view().inject(this);

        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar);
            if (isShowBacking()) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            //设置默认的标题不显示
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        //注册网络状态监听广播
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);
        isRegistered = true;
    }


    /**
     * 是否显示返回按钮
     */
    protected boolean isShowBacking() {

        return true;
    }

    @Override
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mToolbar != null && mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        } else {
            super.setTitle(title);
        }
    }

    public void showWaitDialog() {
        showWaitDialog("正在加载...");
    }

    public void showWaitDialog(String msg) {
        if (this.isFinishing() || this.isDestroyed()) {
            return;
        }
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(this, R.style.Translucent_Theme);
//        waitingDialog.setTitle("我是一个等待Dialog");
            waitingDialog.setMessage(msg);
            waitingDialog.setIndeterminate(true);
            //waitingDialog.setCancelable(false);
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        waitingDialog.show();
    }

    public void hideWaitDialog() {
        if (waitingDialog != null && !(this.isFinishing() || this.isDestroyed())) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }

    public void snackError(String msg) {
        snack(msg, Prompt.ERROR);
    }

    private void snack(String msg, Prompt prompt) {
        snack(msg, prompt, null);
    }

    private void snack(String msg, Prompt prompt, SnackbarAction action) {
        final ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        TSnackbar snackbar = TSnackbar.make(viewGroup, msg, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(prompt);
        if (action != null) {
            snackbar.setAction(action.title, action.onClickListener);
        }
        snackbar.show();
    }

    public boolean checkNetwork() {
        if (!NetworkUtils.isAvailable(this)) {
            snackError("网络未连接");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        hideWaitDialog();
        onBack(map);
    }

    private void onBack(Map<String,String> map){
        if(map!=null){
            Set<String> keySet = map.keySet();
            Iterator<String> it = keySet.iterator();
            while(it.hasNext()){
                String key = it.next();
                String value = map.get(key);
                Intent in = new Intent();
                in.putExtra(key,value);
                setResult(RESULT_OK,in);
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑
        if (isRegistered) {
            unregisterReceiver(netWorkChangReceiver);
        }
    }



}

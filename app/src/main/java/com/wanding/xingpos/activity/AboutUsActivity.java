package com.wanding.xingpos.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.utils.Utils;
import com.wanding.xingpos.version.util.HttpURLConUtil;
import com.wanding.xingpos.version.util.UpdateInfo;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * 悦收银版本信息Activity
 */
@ContentView(R.layout.activity_setting_about)
public class AboutUsActivity extends BaseActivity implements OnClickListener {


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



	@ViewInject(R.id.setting_aboutapp_tvVersionName)
	TextView tvVersionName;
	@ViewInject(R.id.setting_aboutapp_layoutGetVsersion)
	RelativeLayout getVersionCode;



	UpdateInfo info;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private String posProvider;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("关于悦收银");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		posProvider = MainActivity.posProvider;

		initView();

		initListener();
	}


	/**
	 * 初始化界面控件
	 */
	private void initView(){
		try {
			tvVersionName.setText("悦收银："+getVersionName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tvVersionName.setText("悦收银：1.01");
		}

	}

	private void initListener(){
		imgBack.setOnClickListener(this);
		getVersionCode.setOnClickListener(this);
	}

	 /*
     * 获取当前程序的版本号
     */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager =getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }


	/*
	 * 从服务器获取xml解析并进行版本号比对
	 */
	private void checkVersionTask(){


		new Thread(){
			@Override
			public void run() {
                try {
                    String versionName = "";
                    try {
                        versionName = Utils.getVersionName(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //获取服务器保存版本信息的路径
                    String path = "";
                    if(posProvider.equals(Constants.NEW_LAND)){
                        path = NitConfig.getNEWLANDUrl;
                        Log.e("新大陆版本更新地址：","-----------");
                    }else if(posProvider.equals(Constants.FUYOU_SF)){
                        path = NitConfig.getFUYOUUrl;
                        Log.e("富友版本更新地址：","-----------");
                    }
                    //解析xml文件封装成对象
                    info =  HttpURLConUtil.getUpdateInfo(path);
                    Log.i(TAG,"版本号为："+info.getVersion());
                    String xmlVersionName = info.getVersion();
                    if(xmlVersionName.equals(versionName)){
                        Log.i(TAG,"版本号相同无需升级");
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }else{
                        Log.i(TAG,"版本号不同 ,提示用户升级 ");
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
			};

		}.start();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					//对话框提示用户升级程序
					showUpdateDialog();
					break;
				case 2:
					Toast.makeText(activity,"当前已是最新版本！",Toast.LENGTH_LONG).show();
					break;
					default:
						break;
			}
		}

	};

	/**
	 * 弹出版本升级提示框
	 */
	private void showUpdateDialog(){
		View view = LayoutInflater.from(this).inflate(R.layout.update_hint_dialog, null);
		//版本号：
		TextView tvVersion=(TextView) view.findViewById(R.id.update_hint_tvVersion);
		tvVersion.setText("v"+info.getVersion());
		//描述信息

		//操作按钮
		final Button btUpdate = (Button) view.findViewById(R.id.update_hint_btUpdate);
		final Dialog mDialog = new Dialog(this,R.style.dialog);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams params = mDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		mDialog.setContentView(view);
		btUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();

			}
		});
		//点击屏幕和物理返回键dialog不消失
		mDialog.setCancelable(false);
		mDialog.show();
	}

	
	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.setting_aboutapp_layoutGetVsersion:
			checkVersionTask();
			break;
			default:
				break;
		}
	}
}

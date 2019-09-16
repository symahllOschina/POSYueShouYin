package com.wanding.xingpos.version.util;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.wanding.xingpos.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 *  异步下载文件 
 */
public class DownLoadAsyncTask extends AsyncTask<String, Integer, byte[]>{


    private boolean flag; //是否被取消
    private Context context;
    private Handler handler;
    private UpdateInfo info;

	private Dialog mDialog;
    private TextView tvProgress;
    private TextView tvMessage;
    
    private int lenghtOfFile;

    private ProgressBar mProgressBar;
    
    
    
    
    public DownLoadAsyncTask(Context context,Handler handler,UpdateInfo info) {
		super();
		this.context = context;
		this.handler = handler;
		this.info = info;

		showUpdateDialog();

	}

	/**
     * 任务执行之前回调
     */
    @Override
    protected void onPreExecute() {

        flag = true;

    }
    
	@Override
	protected byte[] doInBackground(String... params) {
		int count;//读取当前批次的字节数
		try {
			//创建URL对象，用于访问网络资源
			URL url = new URL(params[0]);
			URLConnection conexion = url.openConnection();
			conexion.connect();
			//获得总长度
			lenghtOfFile = conexion.getContentLength();
			Log.e("文件总长度：",lenghtOfFile+"");
			//开始读取数据
			InputStream input = new BufferedInputStream(url.openStream());
			//写入数据对象并指定写入路径文件
			OutputStream output = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"businfoentry.apk"));
			//每次读取字节数
			byte data[] = new byte[1024];
			int total = 0;//每次读取的数据
			while ((count = input.read(data)) != -1) {
				//指针向后移
				total += count;
				//计算进度
				int progress = (int)((double)total / lenghtOfFile * 100);//先计算出百分比在转换成整型
				//更新进度
				publishProgress(progress, total, lenghtOfFile);
				//写入文件
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
		}
		catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 更新进度条回调
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {

		//设置进度条进度
		mProgressBar.setProgress(values[0]);
		tvProgress.setText("已下载"+values[0].toString()+"%");
		tvMessage.setText("文件共"+lenghtOfFile/1024/1024+"M");

	}
	
	/**
	 * 任务被取消时回调
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	/**
	 *  任务结束后回调
	 */
	@Override
	protected void onPostExecute(byte[] result) {
		mDialog.dismiss();
		//通知程序安装新版本
		Message msg = new Message();
		msg.what=2;
        handler.sendMessage(msg);
	}


	/**
	 * 弹出版本升级提示框
	 */
	private void showUpdateDialog(){
		/*View view = LayoutInflater.from(context).inflate(R.layout.update_hint_dialog, null);
		//版本号：
		TextView tvVersion=(TextView) view.findViewById(R.id.update_hint_tvVersion);
		tvVersion.setText("v"+info.getVersion());
		//描述信息

		//进度条
		mProgressBar = view.findViewById(R.id.update_hint_progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		RelativeLayout layoutMsg = view.findViewById(R.id.update_hint_layoutMsg);
		layoutMsg.setVisibility(View.VISIBLE);
		//进度条信息
		tvProgress = view.findViewById(R.id.update_hint_percent);
		tvMessage = view.findViewById(R.id.update_hint_number);
		//操作按钮
		final Button btUpdate = (Button) view.findViewById(R.id.update_hint_btUpdate);
		btUpdate.setText("正在下载...");

		mDialog = new Dialog(context,R.style.dialog);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams params = mDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		mDialog.setContentView(view);
		btUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


			}
		});
		//点击屏幕和物理返回键dialog不消失
		mDialog.setCancelable(false);
		mDialog.show();*/
	}


}

package com.wanding.xingpos.card.stock.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.card.stock.bean.CheckCardStockResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 核销劵界面
 */
@ContentView(R.layout.activity_check_card_stock)
public class CheckCardStockActivity extends BaseActivity implements OnClickListener {

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


	@ViewInject(R.id.check_card_stock_etNum)
	private EditText etCode;
	@ViewInject(R.id.check_card_stock_imgScan)
	private ImageView imagScan;
	@ViewInject(R.id.check_card_stock_tvOk)
	private TextView tvOk;

	private String posProvider;
	private UserLoginResData loginInitData;

	private String scanCodeStr;//扫描返回结果
	private String etCodeTextStr;//输入框内容

	AidlDeviceService aidlDeviceService = null;
	AidlPrinter aidlPrinter = null;
    public AidlScanner aidlScanner=null;
    /**  cameraType为true表示打开后置摄像头，fasle为前置摄像头 */
	private boolean cameType = true;

	/**
	 * 新大陆(打印机，扫码摄像头)AIDL服务
	 */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "bind device service");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "unbind device service");
            aidlDeviceService = null;
        }
    };

	/**
	 * 富友(打印机)AIDL服务
	 */
	private PrintInterface printService = null;
	private PrintReceiver printReceiver;
	private ServiceConnection printServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			printService = PrintInterface.Stub.asInterface(arg1);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}

	};

	class PrintReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("result");
//			etBack.setText("reason："+result);
		}
	}





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("核劵");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.VISIBLE);
		tvOption.setText("核销记录");
		posProvider = MainActivity.posProvider;
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil_p = new SharedPreferencesUtil(activity, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil_p.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}
		Intent intent = getIntent();
		loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");


		if(posProvider.equals(Constants.NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(Constants.FUYOU_SF)){
			initPrintService();
		}
		initListener();
	}

	/**
	 * 初始化界面控件
	 */
	private void initListener(){

		imgBack.setOnClickListener(this);
		tvOption.setOnClickListener(this);
		imagScan.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(posProvider.equals(Constants.NEW_LAND)){
			unbindService(serviceConnection);
			aidlPrinter=null;
			aidlScanner = null;
		}else if(posProvider.equals(Constants.FUYOU_SF)){
			if(null != printReceiver){
				unregisterReceiver(printReceiver);
			}
			unbindService(printServiceConnection);
		}
		Log.e(TAG, "释放资源成功");
	}
	/**
     * 绑定服务
     */
    public void bindServiceConnection() {
        bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);
//        showMsgOnTextView("服务绑定");
        Log.e("绑定服务", "绑定服务1");
    }

	private void initPrintService(){
		printRegisterReceiver();
		Intent printIntent = new Intent(/*"com.fuyousf.android.fuious.service.PrintInterface"*/);
		printIntent.setAction("com.fuyousf.android.fuious.service.PrintInterface");
		printIntent.setPackage("com.fuyousf.android.fuious");
		bindService(printIntent, printServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void printRegisterReceiver(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.fuyousf.android.fuious.service.print");
		printReceiver = new PrintReceiver();
		registerReceiver(printReceiver, intentFilter);
	}

	/**
	 * 初始化打印设备
	 */
	public void getPrinter() {
		Log.i(TAG, "获取打印机设备实例...");
		try {
			aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
//            showMsgOnTextView("初始化打印机实例");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    
    /**
     * 初始化扫码设备
     */
    public void initScanner() {
        try {
            if (aidlScanner == null){
				aidlScanner = AidlScanner.Stub.asInterface(aidlDeviceService.getScanner());
			}
//            showMsgOnTextView("初始化打扫码实例");
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

	/**
	 * 前置扫码
	 */
	public void frontscan(){
		try {
			Log.i(TAG, "-------------scan-----------");
			aidlScanner= AidlScanner
					.Stub.asInterface(aidlDeviceService.getScanner());
			int time=10;//超时时间
			aidlScanner.startScan(ScanConstant.ScanType.FRONT, time, new AidlScannerListener.Stub() {

				@Override
				public void onScanResult(String[] arg0) throws RemoteException {
//                    showMsgOnTextView("onScanResult-----"+arg0[0]);
					Log.w(TAG,"onScanResult-----"+arg0[0]);
					scanCodeStr = arg0[0];
					//如果扫描的二维码为空则不执行支付请求
					if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
						String auth_no = scanCodeStr;
						Log.e("前置扫码值：", auth_no);
						int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
						String text = auth_no;
						sendMessage(msg,text);

					}else{

					}

				}

				@Override
				public void onFinish() throws RemoteException {
					ToastUtil.showText(activity,"扫码失败！",1);

				}

				@Override
				public void onError(int arg0, String arg1) throws RemoteException {
					ToastUtil.showText(activity,"扫码失败！",1);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showText(activity,"扫码失败！",1);
		}
	}

	/**
	 * 后置扫码
	 */
	public void backscan(){
		try {
			Log.i(TAG, "-------------scan-----------");
			aidlScanner= AidlScanner
					.Stub.asInterface(aidlDeviceService.getScanner());
			aidlScanner.startScan(ScanConstant.ScanType.BACK, 10, new AidlScannerListener.Stub() {

				@Override
				public void onScanResult(String[] arg0) throws RemoteException {
//					showMsgOnTextView("onScanResult-----"+arg0[0]);
					Log.w(TAG,"onScanResult-----"+arg0[0]);

					scanCodeStr = arg0[0];
					if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
						//auth_no	授权码（及扫描二维码值）
						String auth_no = scanCodeStr;
						Log.e("后置扫码值：", auth_no);

						int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
						String text = auth_no;
						sendMessage(msg,text);

					}else{

						Log.e("后置扫码值：", "为空");
						ToastUtil.showText(activity,"扫码失败！",1);
					}

				}

				@Override
				public void onFinish() throws RemoteException {
					ToastUtil.showText(activity,"扫码失败！",1);

				}

				@Override
				public void onError(int arg0, String arg1) throws RemoteException {
					ToastUtil.showText(activity,"扫码失败！",1);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showText(activity,"扫码失败！",1);
		}
	}


	

	
	/** 获取自己生成的订单号   */
	private void getWriteOffQueryCode(){

		etCodeTextStr = etCode.getText().toString().trim();
		if(Utils.isEmpty(etCodeTextStr)){
			ToastUtil.showText(activity,"核销劵码不能为空！",1);
			return;
		}
		showWaitDialog("核劵中...");
		final String url = NitConfig.writeOffQueryCodeUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("mid",loginInitData.getMid());
					userJSON.put("code",etCodeTextStr);
					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content);
					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e(TAG, jsonStr);
					int msg = NetworkUtils.MSG_WHAT_ONE;
					String text = jsonStr;
					sendMessage(msg,text);
				} catch (JSONException e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
				}catch (IOException e){
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
				}
				
			};
		}.start();
	}
	
	/** 核劵请求  */
	private void writeOffConsumeCode(final int id){
		final String url = NitConfig.writeOffConsumeCodeUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("mid",loginInitData.getMid());
					userJSON.put("code",etCodeTextStr);
					userJSON.put("couponId",String.valueOf(id));
					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content);
					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e(TAG, jsonStr);
					int msg = NetworkUtils.MSG_WHAT_TWO;
					String text = jsonStr;
					sendMessage(msg,text);
				} catch (JSONException e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
				}catch (IOException e){
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
				}
				
			};
		}.start();
	}

	private void sendMessage(int what,String text){
		Message msg = new Message();
		msg.what = what;
		msg.obj = text;
		mHandler.sendMessage(msg);
	}

	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String errorJsonText = "";
			switch (msg.what) {
			case NetworkUtils.MSG_WHAT_ONEHUNDRED://新大陆扫码返回
				String auth_no = (String) msg.obj;
				etCode.setText(auth_no);
				break;
			case NetworkUtils.MSG_WHAT_ONE:
				String writeOffStateJsonStr=(String) msg.obj;
				writeOffStateJsonStr(writeOffStateJsonStr);
				break;
			case NetworkUtils.MSG_WHAT_TWO:
				String writeOffResStr=(String) msg.obj;
                writeOffResStr(writeOffResStr);
				etCode.setText("");
				hideWaitDialog();
				break;
				case NetworkUtils.REQUEST_JSON_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					etCode.setText("");
					break;
				case NetworkUtils.REQUEST_IO_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					etCode.setText("");
					break;
				case NetworkUtils.REQUEST_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					etCode.setText("");
					break;
				default:
					break;

			}
		};
	};

	/**
	 * 检验核销劵码有效性
	 */
	private void writeOffStateJsonStr(String jsonStr){
		//{"data":{},"message":"该码不可用","status":300}
		//{"data":{"resultMap":{"code":"548986373384","description":null,"startTime":"1554825600","endTime":"1554998399","id":0,"title":"pos核销劵","logoUrl":null,"status":"1","statusCode":200,"errorMsg":"查询成功"}},"message":"查询成功","status":200}
		try {
			JSONObject job = new JSONObject(jsonStr);
			String status = job.getString("status");
			String message = job.getString("message");
			if("200".equals(status)){
				String dataJson = job.getString("data");
				JSONObject dataJob = new JSONObject(dataJson);
				String resultMapJson = dataJob.getString("resultMap");
				JSONObject resultMapJob = new JSONObject(resultMapJson);
				int id = resultMapJob.getInt("id");
				//核劵
				writeOffConsumeCode(id);
			}else{
				hideWaitDialog();
				if(Utils.isNotEmpty(message)){
					ToastUtil.showText(activity,message,1);
				}else{
					ToastUtil.showText(activity,"该劵不可用！",1);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
     * 核销
     */
	private void writeOffResStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");
                Gson gson  =  GsonUtils.getGson();
				CheckCardStockResData checkCardStockResData = gson.fromJson(dataJson, CheckCardStockResData.class);
				/**
				 * 下面是调用帮助类将一个对象以序列化的方式保存
				 * 方便我们在其他界面调用，类似于Intent携带数据
				 */
				try {
					MySerialize.saveObject("writeOffOrder",getApplicationContext(),MySerialize.serialize(checkCardStockResData));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "writeOff");
				Boolean scanPayValue = true;
				sharedPreferencesUtil.put("scanPayYes", scanPayValue);


                //打印核销小票
                printWiteOffTicket(checkCardStockResData);
				ToastUtil.showText(activity,"核销成功！",1);
            }else{
                hideWaitDialog();
                if(Utils.isNotEmpty(message)){
                    ToastUtil.showText(activity,message,1);
                }else{
                    ToastUtil.showText(activity,"核销失败！",1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打印核销小票
     */
    private void printWiteOffTicket(CheckCardStockResData checkCardStockResData){
        if(Constants.NEW_LAND.equals(posProvider)){

        }else if(Constants.FUYOU_SF.equals(posProvider)){
        	/**
			 * isMakeUp:是否为补打，""正常打印，"C"重打印
			 */
        	String isMakeUp = "";
            FuyouPrintUtil.checkCardStockPrintText(activity,printService,checkCardStockResData,loginInitData,isMakeUp);
        }
    }
	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.menu_title_tvOption:
			in = new Intent();
			in.setClass(activity,CheckCardStockRecodeListActivity.class);
			in.putExtra("userLoginData",loginInitData);
			startActivity(in);
			break;
		case R.id.check_card_stock_imgScan:
			etCode.setText("");
			if(posProvider.equals(Constants.NEW_LAND)){
				initScanner();
				if(cameType){
					Log.e("扫码调用：", "后置摄像头");
					backscan();
				}else{
					Log.e("扫码调用：", "前置摄像头");
					frontscan();
				}
			}else if(posProvider.equals(Constants.FUYOU_SF)){
				FuyouPosServiceUtil.scanReq(activity);
			}

			
			break;
		case R.id.check_card_stock_tvOk:
			getWriteOffQueryCode();
			break;
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();
		/**
		 * 富友POS扫码返回
		 */
		if (requestCode == FuyouPosServiceUtil.SCAN_REQUEST_CODE) {
			if(bundle != null){
				switch (resultCode) {
					// 扫码成功
					case Activity.RESULT_OK:

						scanCodeStr = bundle.getString("return_txt");//扫码返回数据
						Log.e("扫描返回扫描结果：", scanCodeStr);
						etCode.setText(scanCodeStr);
						break;
					// 扫码取消或失败
					case Activity.RESULT_CANCELED:

						String reason = "扫码取消";
						if (data != null) {
							Bundle b = data.getExtras();
							if (b != null) {
								reason = (String) b.get("reason");
							}
						}
						if (reason != null) {
							Log.d("reason", reason);
							ToastUtil.showText(activity,reason,1);
						}
						break;
					default:
						break;

				}
			}else{
				ToastUtil.showText(activity,"扫码失败！",1);
			}

		}
	}
}

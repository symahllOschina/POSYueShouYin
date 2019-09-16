package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.bean.PosRefundReqData;
import com.wanding.xingpos.bean.PosRefundResData;
import com.wanding.xingpos.bean.ScanPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.EditTextUtils;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.RandomStringGenerator;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.Calendar;

import static com.wanding.xingpos.Constants.FUYOU_SF;

/** 扫码退款界面 */
@ContentView(R.layout.activity_scan_refund)
public class ScanRefundActivity extends BaseActivity implements OnClickListener {

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




	@ViewInject(R.id.scan_refund_layoutRefundOrderId)
	RelativeLayout layoutOrderId;
	@ViewInject(R.id.scan_refund_layoutRefundMoney)
	LinearLayout layoutMoney;
	@ViewInject(R.id.scan_refund_etOrderId)
	EditText etOrderId;
	@ViewInject(R.id.scan_refund_etOrderMoney)
	EditText etOrderMoney;
	@ViewInject(R.id.scan_refund_imagScan)
	ImageView imagScan;
	@ViewInject(R.id.scan_refund_tvOk)
	TextView tvOk;


	/** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
	private String printNum = "printNumNo";
	/**  打印字体大小 isDefault:true默认大小，false即为大字体 */
	private boolean isDefault = true;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private String posProvider;
	private UserLoginResData posPublicData;

//	private String etMoneyTextStr;
	AidlDeviceService aidlDeviceService = null;
    AidlPrinter aidlPrinter = null;
    public AidlScanner aidlScanner=null;
    /**  cameraType为true表示打开后置摄像头，fasle为前置摄像头 */
	private boolean cameType = true;
    // 主控制类，所有合成控制方法从这个类开始
    public static MySyntherizer synthesizer;
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



	/**
	 * 扫描返回结果
	 */
	private String scanCodeStr;

	/** 退款步骤 refundOption = 1表示查询订单，2表示输入金额退款  */
	private int refundOption = 1;


	/**  退款时查单返回的支付方式（仅支持富友POS机，打印小票时用）  */
	private String refund_payTypeStr;

	private JSONObject dataJsonObj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("扫码退款");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		synthesizer = MainActivity.synthesizer;
		posProvider = MainActivity.posProvider;
		Intent intent = getIntent();
		posPublicData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(activity, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}

		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "printing");
		//取出保存的默认值
		printNum = (String) sharedPreferencesUtil.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil.getSharedPreference("isDefaultKey", isDefault);



		if(Constants.NEW_LAND.equals(posProvider)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(Constants.FUYOU_SF.equals(posProvider)){
			initPrintService();
		}

		initView();
		initListener();
	}

	/**
	 * 初始化界面控件
	 */
	private void initView(){

		//先隐藏退款金额输入框
		layoutMoney.setVisibility(View.GONE);

		//限制金额输入框只能输入金额
		EditTextUtils.setPricePoint(etOrderMoney);

	}

	private void initListener(){

		imgBack.setOnClickListener(this);
		imagScan.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();

		if(Constants.NEW_LAND.equals(posProvider)){
			unbindService(serviceConnection);
			aidlPrinter=null;
			aidlScanner = null;
		}else if(Constants.FUYOU_SF.equals(posProvider)){
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
            if (aidlScanner == null)
			{
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


	
	 /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String total) {
        String text = "扫码退货"+total+"元";
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        	Log.e("error code :", result+" method:" + method );
        }
    }
	
	/** 获取自己生成的订单号   */
	private void getRefundOrderId(final String etOrderIdTextStr){
		showWaitDialog("正在查询");
		final String url = NitConfig.getPosPayOrderId;
		new Thread(){
			@Override
			public void run() {

				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("refundCode",etOrderIdTextStr);
					if(Constants.NEW_LAND.equals(posProvider)){
						userJSON.put("SDKType","NEWLAND_SDK");
					}else if(Constants.FUYOU_SF.equals(posProvider)){
						userJSON.put("SDKType","FUIOU_SDK");
					}
					String content = String.valueOf(userJSON);
					Log.e("查询订单请求参数：", content);

					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e("查询订单返回字符串结果：", jsonStr);
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
	
	/** 扫描结果发起退款请求  */
	private void payRequestMethood(final PosRefundReqData posBean){
		showWaitDialog("退款中");
		final String url = NitConfig.refundUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("pay_ver",posBean.getPay_ver());
					userJSON.put("pay_type",posBean.getPay_type());
					userJSON.put("service_id",posBean.getService_id());
					userJSON.put("merchant_no",posBean.getMerchant_no());
					userJSON.put("terminal_id",posBean.getTerminal_id());
					userJSON.put("terminal_trace",posBean.getTerminal_trace());
					userJSON.put("terminal_time",posBean.getTerminal_time());
					userJSON.put("refund_fee",posBean.getRefund_fee());
					userJSON.put("out_trade_no",posBean.getOut_trade_no());
					userJSON.put("operator_id",posBean.getOperator_id());
					userJSON.put("key_sign",posBean.getKey_sign());
					String content = String.valueOf(userJSON);
					Log.e("退款请求参数：", content);

					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e("退款返回字符串结果：", jsonStr);
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
		handler.sendMessage(msg);
	}
	
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String errorJsonText = "";
			switch (msg.what) {
			case NetworkUtils.MSG_WHAT_ONEHUNDRED://新大陆扫码返回
				String auth_no = (String) msg.obj;
				etOrderId.setText(auth_no);
				break;
			case NetworkUtils.MSG_WHAT_ONE:
				String orderIdJSONStr=(String) msg.obj;
				//{"data":{"orderId":"20180813180710457012455681976813","refundfCode":"000079","payWay":"WX","way":"SDK"}}
				orderIdJSONStr(orderIdJSONStr);
				hideWaitDialog();
				etOrderId.setText("");
				break;
			case NetworkUtils.MSG_WHAT_TWO:
				String refundJsonStr=(String) msg.obj;
				refundJsonStr(refundJsonStr);
				hideWaitDialog();
        		etOrderId.setText("");
				break;
			case NetworkUtils.REQUEST_JSON_CODE:
				errorJsonText = (String) msg.obj;
				ToastUtil.showText(activity,errorJsonText,1);
				hideWaitDialog();
				etOrderId.setText("");
				break;
			case NetworkUtils.REQUEST_IO_CODE:
				errorJsonText = (String) msg.obj;
				ToastUtil.showText(activity,errorJsonText,1);
				hideWaitDialog();
				etOrderId.setText("");
				break;
			case NetworkUtils.REQUEST_CODE:
				errorJsonText = (String) msg.obj;
				ToastUtil.showText(activity,errorJsonText,1);
				hideWaitDialog();
				etOrderId.setText("");
				break;
			default:
				break;
			}
		};
	};

	private void orderIdJSONStr(String jsonStr){
		try {
			JSONObject job = new JSONObject(jsonStr);
			boolean isSuccess = job.getBoolean("isSuccess");
			if(isSuccess){
				refundOption = 2;
				String dataStr = job.getString("data");
				JSONObject dataJob = new JSONObject(dataStr);
				String way = dataJob.getString("way");
				String refundfCode = dataJob.getString("refundfCode");
				String total_feeStr = "";
				dataJsonObj = dataJob;
				//判断支付通道
				if("SDK".equals(way)){
					Log.e("二维码支付服务类型：", "第三方");
					if(Constants.NEW_LAND.equals(posProvider)){
						Log.e("二维码支付服务类型：", "星POS");
						layoutOrderId.setVisibility(View.GONE);
						layoutMoney.setVisibility(View.VISIBLE);

					}else if(Constants.FUYOU_SF.equals(posProvider)){
						Log.e("二维码支付服务类型：", "富友POS");
						layoutOrderId.setVisibility(View.GONE);
						layoutMoney.setVisibility(View.VISIBLE);
						etOrderMoney.setEnabled(false);
						etOrderMoney.setBackgroundColor(ContextCompat.getColor(activity,R.color.gray_e5e5e5));
						etOrderMoney.setHint("   该订单只支持默认全额退");
						etOrderMoney.setHintTextColor(ContextCompat.getColor(activity,R.color.red_d05450));
					}

				}else{
					Log.e("二维码支付服务类型：", "默认自己的");
					layoutOrderId.setVisibility(View.GONE);
					layoutMoney.setVisibility(View.VISIBLE);
				}
			}else{
				String errorMessage = job.getString("errorMessage");
				ToastUtil.showText(activity,errorMessage,1);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void refundJsonStr(String jsonStr){
	//然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}
		Gson gjson  =  GsonUtils.getGson();
		PosRefundResData posResult = gjson.fromJson(jsonStr, PosRefundResData.class);
		//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
		String return_codeStr = posResult.getReturn_code();
		//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
		String return_msgStr = posResult.getReturn_msg();
		//result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
		String result_codeStr = posResult.getResult_code();
		if("01".equals(return_codeStr)){
			if("01".equals(result_codeStr)){

				/**
				 * 下面是调用帮助类将一个对象以序列化的方式保存
				 * 方便我们在其他界面调用，类似于Intent携带数据
				 */
				try {
					MySerialize.saveObject("refundOrder",activity,MySerialize.serialize(posResult));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "scanPay");
				Boolean scanPayValue = true;
				Boolean payServiceType = true;
				String scanPayTypeValue = "refund";//pay:支付，refund:退款
				sharedPreferencesUtil.put("scanPayYes", scanPayValue);
				sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
				sharedPreferencesUtil.put("payServiceType", payServiceType);


				startPrint(posResult);
//        				Toast.makeText(getContext(), "退款成功!", Toast.LENGTH_LONG).show();

			}else if("03".equals(result_codeStr)){
				ToastUtil.showText(activity,"退款中!",1);
			}else{
				intentActivity();
			}
		}else{
			intentActivity();
		}
	}
	
	private void startPrint(PosRefundResData refundResData){
		String totalStr = DecimalUtil.branchToElement(refundResData.getRefund_fee());
		//播放语音
		speak(totalStr);
		/**
		 * 根据pos机厂商不同调用不同的打印设备
		 */
		int index = 1;
		if(Constants.NEW_LAND.equals(posProvider)){
			//初始化打印机
			getPrinter();

            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if("printNumNo".equals(printNum)){
                intentToActivity();
            }else if("printNumOne".equals(printNum)){
                //打印一次
                NewlandPrintUtil.refundSuccessPrintText(activity, aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                intentToActivity();
            }else if("printNumTwo".equals(printNum)){
                //打印两次
                NewlandPrintUtil.refundSuccessPrintText(activity, aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                try {
                    Thread.sleep(NewlandPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showPrintTwoDialog(refundResData);
            }

		}else if(Constants.FUYOU_SF.equals(posProvider)){

            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if("printNumNo".equals(printNum)){
                intentToActivity();
            }else if("printNumOne".equals(printNum)){
                //打印一次
                FuyouPrintUtil.refundSuccessPrintText(activity, printService, refundResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                intentToActivity();
            }else if("printNumTwo".equals(printNum)){
                //打印两次
                FuyouPrintUtil.refundSuccessPrintText(activity, printService, refundResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                try {
                    Thread.sleep(FuyouPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showPrintTwoDialog(refundResData);
            }
		}


		
	}

    /**  打印下一联提示窗口 */
    private void showPrintTwoDialog(final PosRefundResData refundResData){
        View view = LayoutInflater.from(activity).inflate(R.layout.printtwo_dialog_activity, null);
        TextView btok = (TextView) view.findViewById(R.id.printtwo_dialog_tvOk);
        final Dialog myDialog = new Dialog(activity,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = 2;
                if(Constants.NEW_LAND.equals(posProvider)){
                    NewlandPrintUtil.refundSuccessPrintText(activity, aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                }else if(Constants.FUYOU_SF.equals(posProvider)){
                    FuyouPrintUtil.refundSuccessPrintText(activity, printService, refundResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                }

                intentToActivity();
                myDialog.dismiss();

            }
        });
        myDialog.show();
    }
	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.scan_refund_imagScan:
			etOrderId.setText("");
			if(Constants.NEW_LAND.equals(posProvider)){
				initScanner();
				if(cameType){
					Log.e("扫码调用：", "后置摄像头");
					backscan();
				}else{
					Log.e("扫码调用：", "前置摄像头");
					frontscan();
				}
			}else if(Constants.FUYOU_SF.equals(posProvider)){
				FuyouPosServiceUtil.scanReq(activity);
			}

			
			break;
		case R.id.scan_refund_tvOk:
			if(Utils.isFastClick()){
				Log.e("连续点击","return连续点击");
				return;
			}
			Log.e("连续点击","连续点击结束");
			if(refundOption == 1){
				refundStepOne();
			}else if(refundOption == 2){
				refundStepTwo();
			}
			break;
			default:
				break;
		}
	}
	
	/** 退款第一步：（查询订单判断订单来源）  */
	private void refundStepOne(){
		String etOrderIdTextStr = etOrderId.getText().toString().trim();
		if(Utils.isEmpty(etOrderIdTextStr)){
			Toast.makeText(activity, "退款单号不能为空！", Toast.LENGTH_LONG).show();
			return;
		}
		getRefundOrderId(etOrderIdTextStr);
	}

	/** 退款第二步：（输入金额退款）  */
	private void refundStepTwo(){
		String etMoneyTextStr = etOrderMoney.getText().toString().trim();
		try {
			String way = dataJsonObj.getString("way");
			String refundfCode = dataJsonObj.getString("refundfCode");
			String total_feeStr = "";
			if("SDK".equals(way)){
				if(etMoneyTextStr!=null&&!"".equals(etMoneyTextStr)){
					//金额为元
					total_feeStr = DecimalUtil.StringToPrice(etMoneyTextStr);
				}
				if(Constants.NEW_LAND.equals(posProvider)){
					Log.e("二维码支付服务类型：", "星POS");
					NewPosServiceUtil.refundReq(ScanRefundActivity.this, refundfCode,total_feeStr, posPublicData);
				}else if(Constants.FUYOU_SF.equals(posProvider)){
					Log.e("二维码支付服务类型：", "富友POS");
					//支付方式
					refund_payTypeStr = dataJsonObj.getString("payWay");
					//金额
					total_feeStr = FieldTypeUtil.makeFieldAmount(etMoneyTextStr);
					//凭证号
					String oldTraceStr = dataJsonObj.getString("refundfCode");
					//设备号
					String deviceNum = posPublicData.getTrmNo_pos();
					String pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
					FuyouPosServiceUtil.refundReq(ScanRefundActivity.this,refund_payTypeStr,total_feeStr,oldTraceStr,pos_order_noStr);
				}
			}else{
				if(etMoneyTextStr!=null&&!"".equals(etMoneyTextStr)){
					//金额为分
					total_feeStr = DecimalUtil.elementToBranch(etMoneyTextStr);
				}
				PosRefundReqData posBean = PayRequestUtil.refundReq(total_feeStr,refundfCode,posPublicData,posProvider);
				//付款二维码内容(发起退款请求)
				payRequestMethood(posBean);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


	}
	
	/** 跳转目标Activity */
	private void intentToActivity(){
		startActivity(new Intent(activity, RefundSuccessActivity.class));
		finish();
	}
	
	
	private void intentActivity(){
		Intent in = new Intent();
		in.setClass(activity, PayErrorActivity.class);
		in.putExtra("optionTypeStr", "020");
		startActivity(in);
		finish();
	};

	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0210")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail退款成功返回信息：", txndetail);
			Gson gjson  =  GsonUtils.getGson();
			ScanPaymentDate posResult = gjson.fromJson(txndetail, ScanPaymentDate.class);
			/**
			 * 下面是调用帮助类将一个对象以序列化的方式保存
			 * 方便我们在其他界面调用，类似于Intent携带数据
			 */
			try {
				MySerialize.saveObject("refundOrder",activity,MySerialize.serialize(posResult));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "scanPay");
			Boolean scanPayValue = true;
			Boolean payServiceType = false;
			String scanPayTypeValue = "refund";//pay:支付，refund:退款
			sharedPreferencesUtil.put("scanPayYes", scanPayValue);
			sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
			sharedPreferencesUtil.put("payServiceType", payServiceType);

			try {
				JSONObject job = new JSONObject(txndetail);
				String transamount = job.getString("transamount");
				speak(transamount);
				intentToActivity();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){
		String amountStr = bundle.getString("amount");//金额
		String traceNoStr = bundle.getString("traceNo");//凭证号
		String batchNoStr = bundle.getString("batchNo");//批次号
		String referenceNoStr = bundle.getString("referenceNo");//参考号
		String cardNoStr = bundle.getString("cardNo");//卡号
		String typeStr = bundle.getString("type");//卡类型
		String issueStr = bundle.getString("issue");//发卡行
		String dateStr = bundle.getString("date");//日期
		String timeStr = bundle.getString("time");//时间
		String oldTraceStr = bundle.getString("oldTrace");//原凭证号
		String merchantldStr = bundle.getString("merchantld");//商户号
		String terminalldStr = bundle.getString("terminalld");//终端号
		String merchantNameStr = bundle.getString("merchantName");//商户名称
		String transactionTypeStr = bundle.getString("transactionType");//交易类型

		Log.e("返回的支付金额信息：", amountStr);
		String totalStr = DecimalUtil.branchToElement(amountStr);
		//播放语音
		speak(totalStr);

		//获取系统年份
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR));
		String dateTimeStr = year + dateStr +  timeStr;
		dateStr = year + dateStr;
		Log.e("手动拼接日期时间：",dateTimeStr);
		Log.e("手动拼接日期：",dateStr);

		ScanPaymentDate posResult = new ScanPaymentDate();
		posResult.setOrderid_scan(cardNoStr);
		posResult.setTranslocaldate(dateStr);
		posResult.setTranslocaltime(timeStr);
		posResult.setTransamount(totalStr);
		posResult.setPay_tp(refund_payTypeStr);

		/**
		 * 下面是调用帮助类将一个对象以序列化的方式保存
		 * 方便我们在其他界面调用，类似于Intent携带数据
		 */
		try {
			MySerialize.saveObject("refundOrder",activity,MySerialize.serialize(posResult));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "scanPay");
		Boolean scanPayValue = true;
		Boolean payServiceType = false;
		String scanPayTypeValue = "refund";//pay:支付，refund:退款
		sharedPreferencesUtil.put("scanPayYes", scanPayValue);
		sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
		sharedPreferencesUtil.put("payServiceType", payServiceType);
		intentToActivity();

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
                        etOrderId.setText(scanCodeStr);


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


		/**
		 * 退款返回
		 */
		if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
		    if(bundle != null){
                switch (resultCode) {
                    // 退款成功
                    case Activity.RESULT_OK:
                        if(Constants.NEW_LAND.equals(posProvider)){
                            newlandResult(bundle);
                        }else if(Constants.FUYOU_SF.equals(posProvider)){
                            fuyouResult(bundle);
                        }

                        break;
                    // 支付取消
                    case Activity.RESULT_CANCELED:
                        String reason = "退款取消";
                        if(posProvider.equals(Constants.NEW_LAND)){
                            reason = bundle.getString("reason");
                        }else if(posProvider.equals(FUYOU_SF)){
                            if (data != null) {
                                Bundle b = data.getExtras();
                                if (b != null) {
                                    reason = (String) b.get("reason");
                                }
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
                ToastUtil.showText(activity,"退款返回失败！",1);
            }

		}
	}
}
